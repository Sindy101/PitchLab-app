package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.repository.TunerRepository

class DetectNoteUseCase(
    private val repository: TunerRepository
) {
    suspend operator fun invoke(): TuningResult {
        return repository.analyze()
    }
}
