package com.hai265.timestamper.screens

import kotlinx.io.Sink
import kotlinx.io.Source
import org.koin.core.module.Module

expect fun platform(): String

interface InsetsController {
    fun hideSystemBars()
    fun showSystemBars()
    fun reset()
}

interface OrientationController {
    fun landscape()
    fun portrait()
}

interface FileController {
    suspend fun createFile(fileName: String): Sink
    suspend fun openFilePicker(): Source
}

interface ShareTimestampsSheet {
    suspend operator fun invoke(timestamps: String)
}

expect val platformModule: Module