//package com.example.myapplication
//
//import android.media.AudioFormat
//import android.media.AudioRecord
//import android.media.MediaRecorder
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.example.myapplication.data.audio.PitchDetector
//import com.example.myapplication.domain.model.standardGuitarTuning
//import kotlinx.coroutines.runBlocking
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.File
//import java.io.FileOutputStream
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import kotlin.math.abs
//import kotlin.math.log2
//
///**
// * Instrumented test для записи звука гитарных струн.
// * Работает на любых современных Android, включая 11+.
// */
//@RunWith(AndroidJUnit4::class)
//class GuitarTunerRecordingTest {
//
//    @Test
//    fun recordGuitarStrings() {
//        runBlocking {
//            // -----------------------------
//            // 1. Настройки записи аудио
//            // -----------------------------
//            val sampleRate = 44100
//            val channelConfig = AudioFormat.CHANNEL_IN_MONO
//            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
//            val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
//            val recorder = AudioRecord(
//                MediaRecorder.AudioSource.MIC,
//                sampleRate,
//                channelConfig,
//                audioFormat,
//                bufferSize
//            )
//
//            println("🎸 Запуск записи эталонной настройки гитары. Сыграйте струны по очереди.")
//
//            // -----------------------------
//            // 2. Папка для сохранения файлов
//            // -----------------------------
//            // Используем приватное внешнее хранилище приложения
//            val context = InstrumentationRegistry.getInstrumentation().targetContext
//            val outputDir = File(context.getExternalFilesDir(null), "GuitarTuner")
//            if (!outputDir.exists()) outputDir.mkdirs()
//
//            println("📁 Файлы будут сохранены в: ${outputDir.absolutePath}")
//
//            // -----------------------------
//            // 3. Основной цикл по струнам гитары
//            // -----------------------------
//            for (note in standardGuitarTuning) {
//                println("🎤 Сыграйте струну ${note.name}... Запись идёт 2 секунды.")
//
//                val buffer = ShortArray(bufferSize)
//                val pcmFile = File(outputDir, "${note.name}.pcm")
//                val wavFile = File(outputDir, "${note.name}.wav")
//
//                // -----------------------------
//                // 3.1. Сохраняем PCM
//                // -----------------------------
//                FileOutputStream(pcmFile).use { outputStream ->
//                    recorder.startRecording()
//
//                    val endTime = System.currentTimeMillis() + 2000 // 2 секунды
//                    while (System.currentTimeMillis() < endTime) {
//                        val read = recorder.read(buffer, 0, buffer.size)
//                        if (read > 0) {
//                            val bytes = ByteArray(read * 2)
//                            ByteBuffer.wrap(bytes)
//                                .order(ByteOrder.LITTLE_ENDIAN)
//                                .asShortBuffer()
//                                .put(buffer, 0, read)
//                            outputStream.write(bytes)
//                        }
//                    }
//
//                    recorder.stop()
//                }
//
//                // -----------------------------
//                // 3.2. Конвертируем PCM в WAV
//                // -----------------------------
//                convertPcmToWav(pcmFile, wavFile, sampleRate, 1, 16)
//                pcmFile.delete() // удаляем временный PCM
//
//                // -----------------------------
//                // 3.3. Определяем частоту и строевой звук
//                // -----------------------------
//                val detectedFreq = PitchDetector.detectFrequency(buffer, sampleRate)
//                val centsDiff = 1200 * log2(detectedFreq / note.frequency)
//                val isInTune = abs(centsDiff) < 5
//
//                println("🎶 ${note.name}: ${detectedFreq} Hz (эталон ${note.frequency} Hz) → в строю = $isInTune")
//                println("📁 Файл сохранён: ${wavFile.absolutePath}")
//            }
//
//            recorder.release()
//
//            // -----------------------------
//            // 4. Вывод всех файлов в папке
//            // -----------------------------
//            println("\n📂 Список всех записанных файлов:")
//            outputDir.listFiles()?.forEach { file ->
//                val sizeKB = file.length() / 1024
//                println("• ${file.name} — ${sizeKB} KB — ${file.absolutePath}")
//            }
//        }
//    }
//
//    // ------------------------------------------------------
//    // Функция конвертации PCM в WAV
//    // ------------------------------------------------------
//    private fun convertPcmToWav(
//        pcmFile: File,
//        wavFile: File,
//        sampleRate: Int,
//        channels: Int,
//        bitsPerSample: Int
//    ) {
//        val pcmSize = pcmFile.length().toInt()
//        val totalDataLen = pcmSize + 36
//        val byteRate = sampleRate * channels * bitsPerSample / 8
//
//        FileOutputStream(wavFile).use { out ->
//            val header = ByteArray(44)
//            val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
//
//            buffer.put("RIFF".toByteArray())
//            buffer.putInt(totalDataLen)
//            buffer.put("WAVE".toByteArray())
//            buffer.put("fmt ".toByteArray())
//            buffer.putInt(16)
//            buffer.putShort(1) // PCM
//            buffer.putShort(channels.toShort())
//            buffer.putInt(sampleRate)
//            buffer.putInt(byteRate)
//            buffer.putShort((channels * bitsPerSample / 8).toShort())
//            buffer.putShort(bitsPerSample.toShort())
//            buffer.put("data".toByteArray())
//            buffer.putInt(pcmSize)
//
//            out.write(header)
//            pcmFile.inputStream().use { it.copyTo(out) }
//        }
//    }
//}
