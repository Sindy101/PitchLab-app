package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel для работы с тюнером
class TunerViewModel(
    private val detectNoteUseCase: DetectNoteUseCase
) : ViewModel() {

    // Состояние текущего анализа
    private val _tuningState = MutableStateFlow<TuningResult?>(null)
    val tuningState = _tuningState.asStateFlow()

    private var tuningJob: Job? = null       // Корутин Job для анализа
    private var isRunning = false            // Флаг активности

    // Запуск анализа (если не запущен)
    fun startTuning() {
        if (isRunning) return        // защита от двойного запуска
        isRunning = true

        tuningJob = viewModelScope.launch {
            while (isRunning) {
                val result = detectNoteUseCase()   // Получаем данные анализа
                _tuningState.value = result        // Обновляем поток
                //delay(250)                         // Пауза между измерениями
            }
        }
    }

    // Остановка анализа
    fun stopTuning() {
        isRunning = false
        tuningJob?.cancel()
        tuningJob = null
    }

    // Переключение состояния анализа (кнопка микрофона)
    fun toggleTuning() {
        if (isRunning) stopTuning() else startTuning()
    }

    // Очистка при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        stopTuning()
    }
}
