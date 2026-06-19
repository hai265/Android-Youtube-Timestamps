package com.hai265.timestamper

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.fragment.app.FragmentActivity
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.platformModule
import com.hai265.timestamper.ui.App
import com.hai265.timestamper.ui.theme.AppTheme
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.context.loadKoinModules
import org.koin.core.scope.Scope

class MainActivity : FragmentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        loadKoinModules(platformModule)

        setContent {
            AppTheme {
                val windowSize = calculateWindowSizeClass(this)
                val insetsController = scope.get<InsetsController>()
                val orientationController = scope.get<OrientationController>()
                App(windowSize.widthSizeClass, insetsController, orientationController)
            }
        }
    }
}