package com.hai265.timestamper.ui.screens.youtubeplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ComposeYouTubePlayer(
    videoId: String,
    onCurrentTime: (duration: Duration) -> Unit,
    controller: YouTubePlayerController,
    startingTime: Duration,
    modifier: Modifier = Modifier,
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

                lifecycleOwner.lifecycle.addObserver(this)

                val options = IFramePlayerOptions.Builder(ctx)
                    .controls(1)
                    .fullscreen(1)
                    .autoplay(0)
                    .ivLoadPolicy(3)
                    .build()

                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(player: YouTubePlayer) {
                        controller.player = player
                        player.loadOrCueVideo(
                            lifecycleOwner.lifecycle,
                            videoId,
                            startingTime.inWholeSeconds.toFloat()
                        )
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        super.onCurrentSecond(youTubePlayer, second)
                        onCurrentTime(second.toDouble().toDuration(DurationUnit.SECONDS))
                    }
                }

                initialize(listener, options)

                addFullscreenListener(object : FullscreenListener {
                    val windowInsetsController =
                        activity?.window?.let { WindowCompat.getInsetsController(it, it.decorView) }

                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit
                    ) {
                        val decor = activity?.window?.decorView as? ViewGroup ?: return
                        decor.addView(
                            fullscreenView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                        fullScreenView = fullscreenView
                        windowInsetsController?.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }

                    override fun onExitFullscreen() {
                        val decor = activity?.window?.decorView as? ViewGroup ?: return
                        fullScreenView?.let { decor.removeView(it) }
                        fullScreenView = null
                        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                })

                enableBackgroundPlayback(true)

                playerView = this
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
                    videoId = videoId,
                    onCurrentTime = {},
                    controller = YouTubePlayerController(),
                    startingTime = Duration.ZERO
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
            videoId = "tQDO-uVCl40",
            onCurrentTime = {},
            controller = YouTubePlayerController(),
            startingTime = 120.toDuration(DurationUnit.SECONDS)
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}