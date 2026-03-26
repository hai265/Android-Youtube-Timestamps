package com.hai265.timestamper.data

import java.util.UUID


data class Video(
    val videoId: String,
    val videoTitle: String,
    val thumbnail: String,
)

data class Timestamp(
    val id: String = UUID.randomUUID().toString(),
    val videoId: String,

    val timeMs: Long,
    val label: String,
)
