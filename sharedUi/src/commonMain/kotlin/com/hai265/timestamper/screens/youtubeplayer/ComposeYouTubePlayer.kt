package com.hai265.timestamper.screens.youtubeplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.time.Duration

@Composable
expect fun ComposeYouTubePlayer(
    youtubeId: String,
    onCurrentTime: (duration: Duration) -> Unit,
    controller: YouTubePlayerController,
    startingTime: Duration,
    onFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onTapAddTimestamp: () -> Unit,
    modifier: Modifier = Modifier,
)