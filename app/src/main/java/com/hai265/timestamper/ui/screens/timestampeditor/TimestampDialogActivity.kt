package com.hai265.timestamper.ui.screens.timestampeditor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.uuid.Uuid

class TimestampDialogActivity : FragmentActivity() {
    private val viewmodel: TimestampDialogActivityViewModel by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            AppTheme {
                val state by viewmodel.state.collectAsState()
                when (state) {
                    is State.AddTimestamp -> {
                        TimestampEditorSheet(
                            timestamp = Timestamp(
                                id = Uuid.random(),
                                videoId = (state as State.AddTimestamp).videoId,
                                time = (state as State.AddTimestamp).time
                            ),
                            onDismiss = { finish() },
                            onAddTimestamp = {
                                Toast.makeText(
                                    applicationContext,
                                    "Timestamp successfully created",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }

                    State.Finished -> {
                        finish()
                    }

                    State.Initial -> {

                    }
                }
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.addVideo(it)
                }
            }
        }
    }
}