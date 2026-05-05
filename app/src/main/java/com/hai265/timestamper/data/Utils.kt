package com.hai265.timestamper.data


fun getYouTubeIdFromUrl(url: String): String? {
    val pattern = Regex("(youtu.*be.*)/(watch\\?v=|embed/|v|shorts|)(.*?((?=[&#?])|$))")
    val matches = pattern.find(url)
    return matches?.destructured?.component3()
}

fun getYoutubeThumbnail(videoId: String): String {
    return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
}
