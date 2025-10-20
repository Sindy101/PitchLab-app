package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel для работы с тюнером
class TunerViewModel(
    private val detectNoteUseCase: DetectNoteUseCase
) : ViewModel() {

    private val _tuningState = MutableStateFlow<TuningResult?>(null)
    val tuningState = _tuningState.asStateFlow() // публичный поток

    // Запускает постоянное прослушивание и анализ
    fun startTuning() {
        viewModelScope.launch {
            while (true) {
                val result = detectNoteUseCase()  // получаем анализ
                _tuningState.value = result        // обновляем состояние
                delay(300)                         // обновление каждые 300мс
            }
        }
    }
}
