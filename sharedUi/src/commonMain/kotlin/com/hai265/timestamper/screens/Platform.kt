package com.hai265.timestamper.screens

import org.koin.core.module.Module

expect fun platform(): String

interface InsetsController {
    fun hideSystemBars()
    fun showSystemBars()
}

interface OrientationController {
    fun landscape()
    fun portrait()
}

expect val platformModule: Module