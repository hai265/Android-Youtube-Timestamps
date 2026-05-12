package com.hai265.timestamper.data

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


fun getYouTubeIdFromUrl(url: String): String? {
    val pattern = Regex("(youtu.*be.*)/(watch\\?v=|embed/|v|shorts|)(.*?((?=[&#?])|$))")
    val matches = pattern.find(url)
    return matches?.destructured?.component3()
}

fun getYoutubeThumbnail(videoId: String): String {
    return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
}

fun getYoutubeTimestampFromUrl(url: String): Duration? {
    return Regex("[?&]t=(\\d+)")
        .find(url)
        ?.groupValues
        ?.get(1)
        ?.toInt()
        ?.seconds
}
