package com.hai265.timestamper.screens.youtubeplayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlin.time.Duration

class AndroidYoutubePlayerController : YouTubePlayerController {
    internal var player: YouTubePlayer? = null

    override fun seekTo(duration: Duration) {
        player?.seekTo(duration.inWholeMilliseconds / 1000f)
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun play() {
        player?.play()
    }
}