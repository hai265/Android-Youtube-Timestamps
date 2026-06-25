package com.hai265.timestamper.screens.youtubeplayer

import android.content.Context
import android.content.res.Configuration
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hai265.timestamper.screens.InsetsController

class AndroidInsetsController(
    private val insetsController: WindowInsetsControllerCompat,
    private val context: Context
) :
    InsetsController {

    private val isDarkTheme: Boolean
        get() = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


    override fun hideSystemBars() {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun showSystemBars() {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        insetsController.isAppearanceLightStatusBars = false
    }

    override fun reset() {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        insetsController.isAppearanceLightStatusBars = !isDarkTheme
    }
}