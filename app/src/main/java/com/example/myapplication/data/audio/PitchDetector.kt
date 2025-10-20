package com.example.myapplication.data.audio

import kotlin.math.abs

// Объект для определения частоты (pitch) звука из массива PCM данных
object PitchDetector {

    /**
     * Определяет частоту сигнала в Гц на основе массива PCM данных.
     *
     * @param buffer Массив PCM данных (ShortArray) с аудио-сигналом.
     * @param sampleRate Частота дискретизации аудио (например, 44100 Гц).
     * @return Частота сигнала в Гц, или 0.0 если частота не может быть определена.
     */
    fun detectFrequency(buffer: ShortArray, sampleRate: Int): Double {
        // Преобразуем ShortArray в FloatArray для удобства вычислений
        val floats = buffer.map { it.toFloat() }.toFloatArray()
        val n = floats.size  // Длина буфера

        var bestLag = 0      // Лаг (сдвиг), который даёт наибольшую корреляцию
        var bestCorr = 0.0   // Максимальное значение корреляции

        // 🔹 Алгоритм автокорреляции:
        // Сравниваем сигнал с самим собой со сдвигом lag.
        // Чем выше корреляция, тем вероятнее, что это период сигнала.
        for (lag in 20..(n / 2)) { // Пропускаем слишком маленькие лаги (20) для фильтрации шумов
            var corr = 0.0
            // Вычисляем корреляцию для данного lag
            for (i in 0 until n - lag) {
                corr += floats[i] * floats[i + lag]
            }
            // Сохраняем lag с максимальной корреляцией
            if (corr > bestCorr) {
                bestCorr = corr
                bestLag = lag
            }
        }

        // 🔹 Преобразуем найденный лаг в частоту:
        // Частота = sampleRate / период (lag)
        // Если bestLag = 0 (не найдено), возвращаем 0
        return if (bestLag != 0) sampleRate.toDouble() / bestLag else 0.0
    }
}
