package com.hai265.timestamper.ui

import android.content.Context
import android.widget.Toast
import com.hai265.timestamper.data.repos.VideoResult

fun handleVideoResult(context: Context, videoResult: VideoResult, onSuccess: () -> Unit) {
    when (videoResult) {
        is VideoResult.Success -> {
            Toast.makeText(
                context,
                "Video Successfully Added",
                Toast.LENGTH_LONG
            ).show()
            onSuccess()
        }

        is VideoResult.InvalidUrl -> {
            Toast.makeText(
                context,
                "Invalid Url: ${videoResult.url}",
                Toast.LENGTH_LONG
            ).show()
        }

        is VideoResult.NetworkError -> {
            Toast.makeText(
                context,
                "Network Error: ${videoResult.errorMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}