package com.hai265.timestamper.screens.youtubeplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hai265.timestamper.screens.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//I really  don't want to use android:configChanges="orientation"
//Need a way to save the player on rotation
@Composable
actual fun ComposeYouTubePlayer(
    youtubeId: String,
    onCurrentTime: (duration: Duration) -> Unit,
    controller: YouTubePlayerController,
    startingTime: Duration,
    onFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onTapAddTimestamp: () -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val lifecycleOwner = LocalLifecycleOwner.current

    var playerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    var fullScreenView by remember { mutableStateOf<View?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false

                //Comment this out to enable background playback
                lifecycleOwner.lifecycle.addObserver(this)

                val options = IFramePlayerOptions.Builder(ctx)
                    .controls(1)
                    .fullscreen(1)
                    .autoplay(0)
                    .ivLoadPolicy(3)
                    .build()

                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(player: YouTubePlayer) {
                        (controller as AndroidYoutubePlayerController).player = player
                        player.loadOrCueVideo(
                            lifecycleOwner.lifecycle,
                            youtubeId,
                            startingTime.inWholeSeconds.toFloat()
                        )
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        super.onCurrentSecond(youTubePlayer, second)
                        onCurrentTime(second.toDouble().toDuration(DurationUnit.SECONDS))
                    }
                }

                addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit
                    ) {
                        val decor = activity?.window?.decorView as? ViewGroup ?: return

                        val container = android.widget.FrameLayout(ctx).apply {
                            addView(
                                fullscreenView,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                            val view = android.view.LayoutInflater.from(ctx)
                                .inflate(R.layout.player_custom_view, this, true)
                            view.findViewById<ImageButton>(R.id.add_timestamp_button)
                                .setOnClickListener { onTapAddTimestamp() }
                        }

                        decor.addView(
                            container,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                        fullScreenView = container
                        onFullScreen()
                    }

                    override fun onExitFullscreen() {
                        val decor = activity?.window?.decorView as? ViewGroup ?: return
                        fullScreenView?.let { decor.removeView(it) }
                        fullScreenView = null
                        onExitFullScreen()
                    }
                })

                enableBackgroundPlayback(true)

                playerView = this
                initialize(listener, options)
            }
        }
    )

    DisposableEffect(lifecycleOwner, activity) {
        onDispose {
            val decor = activity?.window?.decorView as? ViewGroup
            fullScreenView?.let { decor?.removeView(it) }
            fullScreenView = null
            playerView?.release()
            playerView = null
        }
    }
}

class ComposeExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var videoId by remember { mutableStateOf("tQDO-uVCl40") }
            Column {
                ComposeYouTubePlayer(
                    youtubeId = videoId,
                    onCurrentTime = {},
                    controller = YouTubePlayerController.NoOp,
                    startingTime = Duration.ZERO,
                    onFullScreen = {},
                    onExitFullScreen = {},
                    onTapAddTimestamp = {},
                )

            }
        }
    }
}

@Preview
@Composable
fun ComposeYoutubePlayerPreview() {
    Column {
        ComposeYouTubePlayer(
            youtubeId = "tQDO-uVCl40",
            onCurrentTime = {},
            controller = YouTubePlayerController.NoOp,
            startingTime = 120.toDuration(DurationUnit.SECONDS),
            onFullScreen = {},
            onTapAddTimestamp = {},
            onExitFullScreen = {},
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}