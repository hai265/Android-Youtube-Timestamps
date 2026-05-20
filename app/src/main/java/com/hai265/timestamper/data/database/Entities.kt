package com.hai265.timestamper.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "videos")
@Serializable
data class Video(
    @PrimaryKey
    @ColumnInfo(name = "video_id")
    val videoId: String,
    @ColumnInfo(name = "video_title")
    val videoTitle: String?,
    val thumbnail: String,
    @ColumnInfo(name = "last_edited")
    val lastEdited: Instant,
    //TODO: Rename to lastPlayedPosition
    @ColumnInfo(name = "last_played")
    val lastPlayed: Duration,
)

@Parcelize
@Serializable
@Entity(
    tableName = "timestamps",
    foreignKeys = [
        ForeignKey(
            entity = Video::class,
            parentColumns = ["video_id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("video_id")
    ]
)
data class Timestamp @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    @ColumnInfo(name = "video_id")
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