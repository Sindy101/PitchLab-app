package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.domain.usecase.DetectNoteUseCase

class TunerViewModelFactory(
    private val detectNoteUseCase: DetectNoteUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TunerViewModel(detectNoteUseCase) as T
    }
}
