package com.example.ambientsoundlevelreader.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log10

class AudioRecorderImpl(private val context: Context): AudioRecorder {
    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
    }
    override fun onStart(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()
            recorder = this
        }
    }

    fun getAmplitude(): Double {
        return if (recorder != null) (20 * Math.log(recorder!!.maxAmplitude / 32767.0)).toInt()
            .toDouble() else 0.0
    }

    fun calculatePdBm(I: Double, Z: Double = 1000.0): Double {
        return 20 * log10(I) + 10 * log10(Z) + 30
    }

    fun getAmp(): Double {
        return calculatePdBm(getAmplitude())
    }

    override fun onStop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}