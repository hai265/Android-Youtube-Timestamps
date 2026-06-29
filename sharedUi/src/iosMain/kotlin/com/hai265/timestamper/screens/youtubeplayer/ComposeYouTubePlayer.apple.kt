package com.hai265.timestamper.screens.youtubeplayer

import androidx.compose.material3.Text

@androidx.compose.runtime.Composable
actual fun ComposeYouTubePlayer(
    youtubeId: String,
    onCurrentTime: (duration: kotlin.time.Duration) -> Unit,
    controller: YouTubePlayerController,
    startingTime: kotlin.time.Duration,
    onFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onTapAddTimestamp: () -> Unit,
    modifier: androidx.compose.ui.Modifier
) {
    Text("TODO")
}