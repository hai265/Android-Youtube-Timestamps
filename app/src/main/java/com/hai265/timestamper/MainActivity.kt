package com.hai265.timestamper

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.hai265.timestamper.screens.FileController
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.ShareTimestampsSheet
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class MainActivity : FragmentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileController = scope.get<FileController>()
        val insetsController = scope.get<InsetsController>()
        val orientationController = scope.get<OrientationController>()
        val shareTimestampSheet = scope.get<ShareTimestampsSheet>()
        enableEdgeToEdge()


        setContent {
            val customTheme = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                        context
                    )
                }

                else -> null
            }
            App(
                insetsController,
                orientationController,
                fileController,
                shareTimestampSheet,
                customTheme,
            )
        }
    }
}