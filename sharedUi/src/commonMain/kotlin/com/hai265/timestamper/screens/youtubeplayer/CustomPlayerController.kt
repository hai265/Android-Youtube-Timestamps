package com.hai265.timestamper.screens.youtubeplayer

import kotlin.time.Duration

interface YouTubePlayerController {
    fun seekTo(duration: Duration)

    fun pause()

    fun play()

    companion object NoOp : YouTubePlayerController {
        override fun seekTo(duration: Duration) {
        }

        override fun pause() {
        }

        override fun play() {
        }

    }
}