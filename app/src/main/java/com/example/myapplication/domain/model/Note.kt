package com.example.myapplication.domain.model

// Модель музыкальной ноты
data class Note(
    val name: String,      // Название ноты, например "E2"
    val frequency: Double  // Частота ноты в Гц
)

// Эталонная настройка гитары (6 струн EADGBE)
val standardGuitarTuning = listOf(
    Note("E2", 82.41),
    Note("A2", 110.00),
    Note("D3", 146.83),
    Note("G3", 196.00),
    Note("B3", 246.94),
    Note("E4", 329.63)
)
