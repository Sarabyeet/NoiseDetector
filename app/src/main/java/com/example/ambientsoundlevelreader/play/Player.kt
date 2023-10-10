package com.example.ambientsoundlevelreader.play

import java.io.File

interface Player {
    fun playFile(file: File)
    fun stop()
}