package com.example.ambientsoundlevelreader.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.ambientsoundlevelreader.service.RecordingService

class RecordingWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // Your task goes here
        val serviceIntent = Intent(applicationContext, RecordingService::class.java)
        applicationContext.startService(serviceIntent)
        return Result.success()
    }
}