package com.hai265.timestamper.data.models

import kotlinx.serialization.Serializable

data class VideoInfo(
    val videoId: String,
    val title: String,
    val thumbnail: String,
)

@Serializable
data class YoutubeYaml(
    val info: Info,
    val tags: List<Tag>
) {


}

@Serializable
data class Info(
    val lastUpdated: Long,
    val title: String,
    val videoId: String,
)

@Serializable
data class Tag(
    val description: String,
    val id: String,
    val seconds: Double,
)