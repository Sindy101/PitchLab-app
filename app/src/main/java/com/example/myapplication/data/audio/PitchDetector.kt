package com.example.myapplication.data.audio

object PitchDetector {

    private const val THRESHOLD = 0.15f

    fun detectFrequency(buffer: ShortArray, sampleRate: Int): Double {

        if (buffer.isEmpty()) return 0.0

        val size = buffer.size
        val audio = FloatArray(size) { buffer[it] / 32768f }
        val tauMax = size / 2
        val yin = FloatArray(tauMax)

        for (tau in 1 until tauMax) {
            var sum = 0f
            for (i in 0 until tauMax) {
                val diff = audio[i] - audio[i + tau]
                sum += diff * diff
            }
            yin[tau] = sum
        }

        var runningSum = 0f
        for (tau in 1 until tauMax) {
            runningSum += yin[tau]
            yin[tau] = yin[tau] * tau / runningSum
        }

        var tauEstimate = -1
        for (tau in 2 until tauMax) {
            if (yin[tau] < THRESHOLD && yin[tau - 1] >= THRESHOLD) {
                tauEstimate = tau
                break
            }
        }

        if (tauEstimate == -1) return 0.0

        return sampleRate.toDouble() / tauEstimate
    }
}
