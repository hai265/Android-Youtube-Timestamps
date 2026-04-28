package com.hai265.timestamper.data.models

import com.hai265.timestamper.data.database.Timestamp
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

data class VideoInfo(
    val videoId: String,
    val title: String,
    val thumbnail: String,
)

@Serializable
data class VideoBackup(
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

@OptIn(ExperimentalTime::class)
fun Timestamp.toTag(): Tag {
    return Tag(
        description = this.description,
        id = this.id.toString(),
        seconds = this.time.inWholeSeconds.toDouble()
    )
}