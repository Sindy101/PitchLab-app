package com.example.myapplication.presentation.viewmodel

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TunerViewModel(
    private val detectNoteUseCase: DetectNoteUseCase,
    private val recorder: AudioRecorder,
    private val context: Context // нужен для проверки разрешения
) : ViewModel() {

    private val _tuningState = MutableStateFlow<TuningResult?>(null)
    val tuningState = _tuningState.asStateFlow()

    private var tuningJob: Job? = null
    private var isRunning = false

    fun startTuning() {
        if (isRunning) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return // разрешение нет

        try {
            recorder.start()
        } catch (e: SecurityException) {
            e.printStackTrace()
            return
        }

        isRunning = true
        tuningJob = viewModelScope.launch {
            while (isRunning) {
                val result = detectNoteUseCase()   // анализ с микрофона
                _tuningState.value = result
                delay(100) // частота обновления ~10 раз в секунду
            }
        }
    }

    fun stopTuning() {
        isRunning = false
        tuningJob?.cancel()
        tuningJob = null
        try {
            recorder.stop()
        } catch (_: SecurityException) {}
    }

    fun toggleTuning() {
        if (isRunning) stopTuning() else startTuning()
    }

    override fun onCleared() {
        super.onCleared()
        stopTuning()
        recorder.release()
    }
}
