package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.TuningResult

// Интерфейс репозитория для анализа звука
interface TunerRepository {
    suspend fun analyze(): TuningResult
}
