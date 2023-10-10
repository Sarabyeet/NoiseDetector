package com.example.ambientsoundlevelreader.record

import java.io.File

interface AudioRecorder {
    fun onStart(outputFile: File)
    fun onStop()
}