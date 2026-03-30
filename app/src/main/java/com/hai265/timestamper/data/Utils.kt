package com.hai265.timestamper.data

import androidx.core.net.toUri

//TODO: I might not need this if I'm just calling the youtube api directly
fun getYouTubeId(url: String): String? {
    val uri = url.toUri()

    return when {
        // https://www.youtube.com/watch?v=VIDEO_ID
        uri.host?.contains("youtube.com") == true &&
                uri.path == "/watch" -> {
            uri.getQueryParameter("v")
        }

        // https://youtu.be/VIDEO_ID
        uri.host?.contains("youtu.be") == true -> {
            uri.lastPathSegment
        }

        // https://www.youtube.com/embed/VIDEO_ID
        uri.path?.startsWith("/embed/") == true -> {
            uri.pathSegments.getOrNull(1)
        }

        // https://www.youtube.com/shorts/VIDEO_ID
        uri.path?.startsWith("/shorts/") == true -> {
            uri.pathSegments.getOrNull(1)
        }

        else -> null
    }
}

fun getYoutubeThumbnail(videoId: String): String {
    return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
}
