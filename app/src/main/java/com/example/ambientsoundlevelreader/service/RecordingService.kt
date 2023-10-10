package com.example.ambientsoundlevelreader.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import com.example.ambientsoundlevelreader.MainActivity
import com.example.ambientsoundlevelreader.R
import com.example.ambientsoundlevelreader.play.PlayerImpl
import com.example.ambientsoundlevelreader.record.AudioRecorderImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class RecordingService: Service() {

    companion object {
        var instance: RecordingService? = null
        private val CHANNEL_ID = "ForegroundServiceChannel"
    }


    private val recorder by lazy {
        AudioRecorderImpl(applicationContext)
    }

    private val player by lazy {
        PlayerImpl(applicationContext)
    }

    private var audioFile: File? = null

    private lateinit var notification: Notification

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service")
            .setContentText("Recording...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        File(cacheDir, "recorded_audio.mp3").also {
            recorder.onStart(it)
            audioFile = it
        }

        return START_NOT_STICKY
    }

    fun getReading(): Double {
        return recorder.getAmplitude()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder.onStop()
    }
}