package com.hai265.timestamper.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration

@Entity(tableName = "videos")
data class Video(
    @PrimaryKey
    val videoId: String,
    val videoTitle: String?,
    val thumbnail: String,
    val lastEdited: Duration,
    val lastPlayed: Duration,
)

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

@Parcelize
data class Timestamp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: String,
    val timeMs: Long,
    val description: String
) : Parcelable