package com.hai265.timestamper.screens

import com.hai265.timestamper.screens.youtubeplayer.AndroidYoutubePlayerController
import com.hai265.timestamper.screens.youtubeplayer.YouTubePlayerController
import org.koin.dsl.module

actual fun platform() = "Android"


actual val platformModule = module {
    factory<YouTubePlayerController> {
        AndroidYoutubePlayerController()
    }
}