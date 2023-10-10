package com.example.ambientsoundlevelreader.play

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class PlayerImpl(private val context: Context): Player {
    private var audioPlayer: MediaPlayer? = null

    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            audioPlayer = this
            start()
        }
    }

    override fun stop() {
        audioPlayer?.stop()
        audioPlayer?.release()
        audioPlayer = null
    }
}