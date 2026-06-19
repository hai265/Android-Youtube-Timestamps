package com.hai265.timestamper.screens.youtubeplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import com.hai265.timestamper.screens.OrientationController

class AndroidOrientationController(private val activity: Activity) : OrientationController {
    override fun landscape() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun portrait() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    }
}