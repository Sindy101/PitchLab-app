package com.example.myapplication.presentation.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.data.repository.InstrumentRepository
import com.example.myapplication.domain.model.Instrument
import com.example.myapplication.domain.model.Note
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.log2

class TunerViewModel(
    private val detectNoteUseCase: DetectNoteUseCase,
    public val recorder: AudioRecorder,
    private val context: Context,
    private val instrumentRepository: InstrumentRepository
) : ViewModel() {

    private val defaultInstrument = Instrument(
        name = "Guitar Standard",
        type = com.example.myapplication.domain.model.InstrumentType.GUITAR,
        strings = listOf(
            Note("E2", 82.41),
            Note("A2", 110.00),
            Note("D3", 146.83),
            Note("G3", 196.00),
            Note("B3", 246.94),
            Note("E4", 329.63)
        )
    )

    private val _instrument = MutableStateFlow<Instrument?>(null)
    val instrument = _instrument.asStateFlow()

    val selectedStringIndex = MutableStateFlow(0)

    private val _tuningState = MutableStateFlow<TuningResult?>(null)
    val tuningState = _tuningState.asStateFlow()

    private var tuningJob: Job? = null
    private var isRunning = false

    init {
        viewModelScope.launch {
            val list = instrumentRepository.getInstruments()
            _instrument.value = list.firstOrNull() ?: defaultInstrument
            selectString(1)
        }
    }

    fun selectString(i: Int) {
        selectedStringIndex.value = i
    }

    fun setInstrument(name: String) {
        viewModelScope.launch {
            val list = instrumentRepository.getInstruments()
            val found = list.find { it.name.contains(name, ignoreCase = true) }
            if (found != null) {
                _instrument.value = found
                selectedStringIndex.value = 1
            }
        }
    }

    fun startTuning() {
        if (isRunning) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) return

        try {
            recorder.start()
        } catch (_: SecurityException) {
            return
        }

        isRunning = true

        tuningJob = viewModelScope.launch {
            while (isRunning) {
                val raw = detectNoteUseCase()
                val selected = selectedStringIndex.value

                val inst = _instrument.value ?: continue
                if (selected == 0) {
                    _tuningState.value = raw
                    continue
                }

                val target = inst.strings[selected - 1]
                val freq = raw.detectedFrequency

                val difference = calculateCentsDifference(freq, target.frequency)
                val isInTune = kotlin.math.abs(difference) <= 5

                _tuningState.value = TuningResult(
                    detectedNote = target,
                    differenceCents = difference,
                    isInTune = isInTune,
                    detectedFrequency = freq
                )

                delay(50)
            }
        }
    }

    private fun calculateCentsDifference(freq: Double, target: Double): Double {
        if (freq <= 0.0) return 0.0
        return 1200 * log2(freq / target)
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
