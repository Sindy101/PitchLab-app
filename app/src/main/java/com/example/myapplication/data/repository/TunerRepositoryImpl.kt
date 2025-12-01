package com.example.myapplication.data.repository

import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.data.audio.PitchDetector
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.TunerRepository
import kotlin.math.abs
import kotlin.math.log2

// Реализация репозитория для тюнера
// Отвечает за захват аудио, определение частоты и вычисление отклонения от эталонной ноты
class TunerRepositoryImpl(
    private val recorder: AudioRecorder,      // Объект для записи аудио
    private val sampleRate: Int = 44100       // Частота дискретизации
) : TunerRepository {

    /**
     * Анализирует текущий звук с микрофона.
     * @return TuningResult - данные о текущей частоте, ближайшей ноте и отклонении в центах.
     */
    override suspend fun analyze(): TuningResult {
        // 🔹 Снимаем аудио с микрофона через AudioRecorder
        val buffer = recorder.captureAudio()

        // 🔹 Если буфер пустой, возвращаем пустой результат
        if (buffer.isEmpty()) return TuningResult(null, 0.0, false, 0.0)

        // 🔹 Проверяем амплитуду сигнала
        val amplitude = buffer.map { kotlin.math.abs(it.toInt()) }.average()
        if (amplitude < 100.0) { // порог, подбирается под твой микрофон
            return TuningResult(null, 0.0, false, 0.0) // считаем тишину
        }

        // 🔹 Определяем частоту с помощью PitchDetector
        val freq = PitchDetector.detectFrequency(buffer, sampleRate)

        // 🔹 Если частота не определена (шум или тишина), возвращаем "пустой" результат
        if (freq == 0.0) {
            return TuningResult(null, 0.0, false, 0.0)
        }

        // 🔹 Находим ближайшую эталонную ноту
        val closest = standardGuitarTuning.minByOrNull { abs(it.frequency - freq) }
            ?: return TuningResult(null, 0.0, false, freq)

        // 🔹 Вычисляем отклонение в центах
        val centsDiff = 1200 * log2(freq / closest.frequency)

        // 🔹 Струна в строю, если отклонение < ±5 центов
        val isInTune = abs(centsDiff) < 5

        return TuningResult(
            closest,
            centsDiff,
            isInTune,
            freq
        )
    }
}
