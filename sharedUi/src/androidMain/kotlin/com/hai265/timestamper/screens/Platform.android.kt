package com.hai265.timestamper.screens

import android.app.Activity
import androidx.core.view.WindowCompat
import com.hai265.timestamper.screens.youtubeplayer.AndroidFileController
import com.hai265.timestamper.screens.youtubeplayer.AndroidInsetsController
import com.hai265.timestamper.screens.youtubeplayer.AndroidOrientationController
import com.hai265.timestamper.screens.youtubeplayer.AndroidShareTimestampsSheet
import com.hai265.timestamper.screens.youtubeplayer.AndroidYoutubePlayerController
import com.hai265.timestamper.screens.youtubeplayer.YouTubePlayerController
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.scope.dsl.activityScope
import org.koin.dsl.module

actual fun platform() = "Android"


actual val platformModule = module {
    factory<YouTubePlayerController> {
        AndroidYoutubePlayerController()
    }

    activityScope {
        factory<InsetsController> {
            AndroidInsetsController(
                WindowCompat.getInsetsController(
                    get<Activity>().window,
                    get<Activity>().window.decorView,
                ), get()
            )
        }
        factory<OrientationController> { AndroidOrientationController(get()) }
        factory<FileController> { AndroidFileController(get()) }
        factory<ShareTimestampsSheet> { AndroidShareTimestampsSheet(androidContext()) }
    }
}