package com.hai265.timestamper

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.hai265.timestamper.ui.App
import com.hai265.timestamper.ui.handleVideoResult
import com.hai265.timestamper.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewmodel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                val windowSize = calculateWindowSizeClass(this)
                App(windowSize.widthSizeClass)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    val videoResult = viewmodel.addVideo(it)
                    withContext(Dispatchers.Main) {
                        handleVideoResult(this@MainActivity, videoResult, {})
                    }
                }
            }
        }
    }
}