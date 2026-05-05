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
data class Backup(
    val videos: List<VideoBackup>
)

@Serializable
data class VideoBackup(
    val lastUpdated: Long,
    val title: String,
    val videoId: String,
    val timestamps: List<TimestampBackup>,
)

@Serializable
data class TimestampBackup(
    val description: String,
    val id: String,
    val seconds: Double,
)

@OptIn(ExperimentalTime::class)
fun Timestamp.toTimestampBackup(): TimestampBackup {
    return TimestampBackup(
        description = this.description,
        id = this.id.toString(),
        seconds = this.time.inWholeSeconds.toDouble()
    )
}