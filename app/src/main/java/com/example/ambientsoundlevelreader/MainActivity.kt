package com.example.ambientsoundlevelreader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.ambientsoundlevelreader.play.PlayerImpl
import com.example.ambientsoundlevelreader.record.AudioRecorderImpl
import com.example.ambientsoundlevelreader.service.RecordingService
import com.example.ambientsoundlevelreader.ui.theme.AmbientSoundLevelReaderTheme
import com.example.ambientsoundlevelreader.worker.RecordingWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val CHANNEL_ID = "ForegroundServiceChannel"

    private val recorder by lazy {
        AudioRecorderImpl(applicationContext)
    }

    private val player by lazy {
        PlayerImpl(applicationContext)
    }

    private var audioFile: File? = null

    private var logAmp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            0
        )

        createNotificationChannel()

        val serviceIntent = Intent(this@MainActivity, RecordingService::class.java)

        val workRequest = PeriodicWorkRequest.Builder(RecordingWorker::class.java, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        setContent {
            AmbientSoundLevelReaderTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Button(onClick = {
                        startService(serviceIntent)
                        logAmp = true


                    }) {
                        Text(text = "Start recording service")
                    }

                    Button(onClick = {
                        stopService(serviceIntent)
                        logAmp = false
                    }) {
                        Text(text = "Stop recording service")
                    }

                    Button(onClick = {
                        player.playFile(audioFile ?: return@Button)
                    }) {
                        Text(text = "Play recording")
                    }

                    Button(onClick = {
                        player.stop()
                    }) {
                        Text(text = "Stop playing recording")
                    }

                    MyScreen()
                }
            }
        }
    }


    @Composable
    fun MyScreen() {
        var text by remember { mutableStateOf("Initial Text") }

        val scope = rememberCoroutineScope()

        Button(onClick = {
            scope.launch {
                while (logAmp) {
                    delay(1000) // delay for 1 second
                    text = "Current amp: ${RecordingService.instance?.getReading()}"
                }
            }
        }) {
            Text("Start amp counter")
        }

        Text(text)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
