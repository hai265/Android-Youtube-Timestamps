package com.hai265.timestamper.data.database

import android.os.Parcelable
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

@Entity(tableName = "videos")
@Serializable
data class Video(
    @PrimaryKey
    val videoId: String,
    val videoTitle: String?,
    val thumbnail: String,
    val lastEdited: Instant,
    //TODO: Rename to lastPlayedPosition
    val lastPlayed: Duration,
)

@Parcelize
@Serializable
@Entity(
    tableName = "timestamps",
    foreignKeys = [
        ForeignKey(
            entity = Video::class,
            parentColumns = ["videoId"],
            childColumns = ["videoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("videoId")
    ]
)
data class Timestamp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: String,
    val time: Duration,
    val description: String
) : Parcelable

@Serializable
data class VideoWithTimestamps(
    @Embedded val video: Video,
    @Relation(
        parentColumn = "videoId",
        entityColumn = "videoId",
    )
    val timestamps: List<Timestamp>
)