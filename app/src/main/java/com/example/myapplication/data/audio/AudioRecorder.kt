package com.example.myapplication.data.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRecorder(
    private val sampleRate: Int = 44100
) {
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var recorder: AudioRecord? = null
    private var isRecording = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun prepareRecorder() {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start() {
        if (recorder == null) prepareRecorder()
        if (!isRecording) {
            recorder?.startRecording()
            isRecording = true
        }
    }

    fun stop() {
        if (isRecording) {
            recorder?.stop()
            isRecording = false
        }
    }

    suspend fun captureAudio(): ShortArray = withContext(Dispatchers.IO) {
        val r = recorder ?: return@withContext ShortArray(0)
        if (!isRecording) return@withContext ShortArray(0)

        val buffer = ShortArray(bufferSize)
        val read = r.read(buffer, 0, bufferSize)

        if (read <= 0) ShortArray(0)
        else buffer
    }

    fun release() {
        stop()
        recorder?.release()
        recorder = null
    }
}
