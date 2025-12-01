package com.example.myapplication.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.data.repository.InstrumentRepository
import com.example.myapplication.domain.usecase.DetectNoteUseCase

class TunerViewModelFactory(
    private val detectNoteUseCase: DetectNoteUseCase,
    private val recorder: AudioRecorder,
    private val context: Context,
    private val instrumentRepository: InstrumentRepository  // <-- добавили
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TunerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TunerViewModel(
                detectNoteUseCase,
                recorder,
                context,
                instrumentRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
