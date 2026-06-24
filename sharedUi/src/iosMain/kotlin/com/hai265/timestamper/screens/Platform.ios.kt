package com.hai265.timestamper.screens

import com.hai265.timestamper.screens.youtubeplayer.YouTubePlayerController
import deps.IosYoutubePlayerController
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platform() = "iOS"

actual val platformModule: Module = module {
    factory<YouTubePlayerController> { IosYoutubePlayerController }
}