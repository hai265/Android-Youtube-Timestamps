package com.hai265.timestamper

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.youtubeplayer.AndroidInsetsController
import com.hai265.timestamper.screens.youtubeplayer.AndroidOrientationController
import com.hai265.timestamper.ui.App
import com.hai265.timestamper.ui.theme.AppTheme
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        loadKoinModules(module {
            factory<InsetsController> { AndroidInsetsController(insetsController) }
            factory<OrientationController> { AndroidOrientationController(this@MainActivity) }
        })

        setContent {
            AppTheme {
                val windowSize = calculateWindowSizeClass(this)
                App(windowSize.widthSizeClass)
            }
        }
    }
}