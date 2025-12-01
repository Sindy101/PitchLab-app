package com.example.myapplication.domain.model

// Результат анализа звука
data class TuningResult(
    val detectedNote: Note?,       // Определённая нота
    val differenceCents: Double,   // Отклонение от эталонной частоты в центах
    val isInTune: Boolean,         // Настроена ли струна (±5 центов)
    val detectedFrequency: Double  // Фактическая измеренная частота
)
