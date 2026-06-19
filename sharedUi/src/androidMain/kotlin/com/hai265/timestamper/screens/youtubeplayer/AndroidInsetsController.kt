package com.hai265.timestamper.screens.youtubeplayer

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hai265.timestamper.screens.InsetsController

class AndroidInsetsController(private val insetsController: WindowInsetsControllerCompat) :
    InsetsController {

    override fun hideSystemBars() {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun showSystemBars() {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        insetsController.isAppearanceLightStatusBars = false
    }
}