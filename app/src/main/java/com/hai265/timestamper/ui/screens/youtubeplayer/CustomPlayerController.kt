package com.hai265.timestamper.ui.screens.youtubeplayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlin.time.Duration

class YouTubePlayerController {
    internal var player: YouTubePlayer? = null

    fun seekTo(duration: Duration) {
        player?.seekTo(duration.inWholeMilliseconds / 1000f)
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun play() {
        player?.play()
    }
}