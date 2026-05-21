package com.hai265.timestamper.data.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Video(
    val youtubeId: String,
    val videoTitle: String?,
    val thumbnail: String,
    val lastEdited: Instant,
    //TODO: Rename to lastPlayedPosition
    val lastPlayed: Duration,
)

@Parcelize
@Serializable
data class Timestamp @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val videoId: String = "",
    val time: Duration = Duration.ZERO,
    val description: String = ""
) : Parcelable

@Serializable
data class VideoWithTimestamps(
    @Embedded val video: Video,
    @Relation(
        parentColumn = "video_id",
        entityColumn = "video_id",
    )
    val timestamps: List<Timestamp>
)