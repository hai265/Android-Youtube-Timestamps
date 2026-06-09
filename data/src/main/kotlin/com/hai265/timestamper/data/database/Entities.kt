package com.hai265.timestamper.data.database

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Video(
    val id: Uuid,
    val youtubeId: String,
    val videoTitle: String?,
    val thumbnail: String,
    val lastEdited: Instant,
    //TODO: Rename to lastPlayedPosition
    val lastPlayed: Duration,
)

@Serializable
data class Timestamp @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val videoId: Uuid,
    val time: Duration = Duration.ZERO,
    val description: String = ""
)

@Serializable
data class VideoWithTimestamps(
    val video: Video,
    val timestamps: List<Timestamp>
)