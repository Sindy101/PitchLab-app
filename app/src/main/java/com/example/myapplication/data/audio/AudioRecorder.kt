package com.example.myapplication.data.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Класс для записи аудио с микрофона устройства
class AudioRecorder(
    // Частота дискретизации в Гц (по умолчанию 44100, стандартное значение для аудио высокого качества)
    private val sampleRate: Int = 44100
) {
    // Определяем минимальный размер буфера для AudioRecord
    // AudioRecord.getMinBufferSize возвращает размер буфера, необходимый для корректной работы с выбранными параметрами
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,    // Запись в моно
        AudioFormat.ENCODING_PCM_16BIT  // 16-битный PCM формат
    )

    // Ссылка на объект AudioRecord
    // Изначально null, так как запись не должна стартовать без явного запроса
    private var recorder: AudioRecord? = null

    // Метод для инициализации AudioRecord
    // Не начинает запись сразу, только готовит объект
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun prepareRecorder() {
        // ⚠️ Важно: AudioRecord требует разрешение RECORD_AUDIO
        // Если разрешение не предоставлено, метод captureAudio может выбросить SecurityException
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,  // Источник аудио — микрофон
            sampleRate,                      // Частота дискретизации
            AudioFormat.CHANNEL_IN_MONO,     // Моно
            AudioFormat.ENCODING_PCM_16BIT,  // 16-бит PCM
            bufferSize                        // Размер буфера
        )
    }

    // Метод для захвата аудио в виде массива PCM данных (ShortArray)
    // Возвращает пустой массив, если объект recorder не создан или если нет разрешения
    // suspend, чтобы можно было вызвать из корутины и не блокировать главный поток
    suspend fun captureAudio(): ShortArray = withContext(Dispatchers.IO) {
        val r = recorder ?: return@withContext ShortArray(0) // Если recorder == null, возвращаем пустой массив
        val buffer = ShortArray(bufferSize)                 // Буфер для чтения аудио
        try {
            r.startRecording()                              // Начинаем запись
            r.read(buffer, 0, bufferSize)                   // Читаем данные в буфер
            r.stop()                                        // Останавливаем запись после захвата
        } catch (e: SecurityException) {                    // Ловим исключения, если нет разрешения на микрофон
            e.printStackTrace()
            // ⚠️ Пользователь мог отклонить разрешение, поэтому возвращаем пустой массив
            return@withContext ShortArray(0)
        }
        buffer                                              // Возвращаем считанные данные
    }

    // Метод для освобождения ресурсов AudioRecord
    // Вызывается, когда запись больше не нужна
    fun release() {
        recorder?.release()  // Освобождаем ресурсы AudioRecord
        recorder = null      // Обнуляем ссылку, чтобы избежать повторного использования
    }
}

/*
================= ВАЖНО =================
На фронтенде необходимо запросить разрешение RECORD_AUDIO у пользователя перед использованием AudioRecorder:

Пример (Kotlin + AndroidX):

if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
    != PackageManager.PERMISSION_GRANTED) {

    // Запрос разрешения у пользователя
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.RECORD_AUDIO),
        REQUEST_RECORD_AUDIO_PERMISSION
    )
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // Разрешение предоставлено, можно использовать AudioRecorder
        } else {
            // Разрешение отклонено — показать сообщение пользователю
        }
    }
}

⚠️ Без этого проверки и запроса разрешения вызов prepareRecorder() и captureAudio() может
выбросить SecurityException.
========================================
*/
