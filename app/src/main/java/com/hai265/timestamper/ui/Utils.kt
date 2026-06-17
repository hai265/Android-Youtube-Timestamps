package com.hai265.timestamper.ui

import android.content.Context
import android.widget.Toast
import com.hai265.timestamper.data.repos.VideoResult
import java.util.Locale
import kotlin.time.Duration


//TODO: Central place to handle adding video and showing toast?
fun handleVideoResult(context: Context, videoResult: VideoResult, onSuccess: () -> Unit) {
    if (videoResult is VideoResult.Success) onSuccess()
    val toastText = when (videoResult) {
        is VideoResult.Success -> "Video Successfully Added"
        is VideoResult.VideoAlreadyExists -> "Video Already Exists"
        is VideoResult.InvalidUrl -> "Invalid Url: ${videoResult.url}"
        is VideoResult.NetworkError -> "Network Error: ${videoResult.errorMessage}"
    }
    Toast.makeText(
        context,
        toastText,
        Toast.LENGTH_LONG
    ).show()
}

fun Duration.formatDurationToHHMMSS(): String =
    this.toComponents { hours, minutes, seconds, _ ->
        if (hours >= 1L) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }