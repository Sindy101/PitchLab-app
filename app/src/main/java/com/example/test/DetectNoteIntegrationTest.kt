//package com.example.myapplication
//
//import com.example.myapplication.data.audio.AudioRecorder
//import com.example.myapplication.data.repository.TunerRepositoryImpl
//import com.example.myapplication.domain.usecase.DetectNoteUseCase
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert
//import org.junit.Test
//import kotlin.math.PI
//import kotlin.math.sin
//
//class DetectGuitarStringsIntegrationTest {
//
//    // FakeAudioRecorder генерирует синусоиду заданной частоты
//    class FakeAudioRecorder(private val freq: Double) : AudioRecorder(44100) {
//        override suspend fun captureAudio(): ShortArray {
//            val sampleRate = 44100
//            val duration = 1.0 // 1 секунда для стабильного FFT
//            val n = (sampleRate * duration).toInt()
//            val amplitude = 0.5 * Short.MAX_VALUE // уменьшенная амплитуда, чтобы не клиповать
//            return ShortArray(n) { i ->
//                (amplitude * sin(2 * PI * freq * i / sampleRate)).toInt().toShort()
//            }
//        }
//    }
//
//    // Эталонные частоты струн
//    private val strings = listOf(
//        "E2" to 82.41,
//        "A2" to 110.00,
//        "D3" to 146.83,
//        "G3" to 196.00,
//        "B3" to 246.94,
//        "E4" to 329.63
//    )
//
//    @Test
//    fun testAllGuitarStrings() = runBlocking {
//        for ((name, freq) in strings) {
//            val recorder = FakeAudioRecorder(freq)
//            val repository = TunerRepositoryImpl(recorder)
//            val useCase = DetectNoteUseCase(repository)
//
//            val result = useCase()
//
//            println("🎸 String $name: detected ${result.detectedNote?.name}, freq=${result.detectedFrequency}, centsDiff=${result.differenceCents}")
//
//            Assert.assertEquals(name, result.detectedNote?.name)
//            Assert.assertTrue(result.isInTune)
//            Assert.assertEquals(freq, result.detectedFrequency, 1.0) // допускаем погрешность 1 Гц
//        }
//    }
//}
