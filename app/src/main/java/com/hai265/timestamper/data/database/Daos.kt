package com.hai265.timestamper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Dao
interface VideoDao {
    fun getAllVideos(): Flow<List<Video>>

    @Transaction
    suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps>

    suspend fun getVideoByYoutubeId(youtubeId: String): Video?

    suspend fun updateLastPlayed(videoId: Uuid, timestamp: Long)

    @Upsert
    suspend fun addVideo(video: Video, userId: Uuid?)

    @Delete
    suspend fun deleteVideo(video: Video)

    suspend fun updateLastEdited(videoId: Uuid, now: Instant)
}

@Dao
interface TimestampDao {
    fun getTimestamps(videoId: Uuid): Flow<List<Timestamp>>

    @Upsert
    suspend fun upsertTimestamp(timestamp: Timestamp): Uuid

    @Delete
    suspend fun deleteTimestamp(timestamp: Timestamp)

    fun getTimestampById(id: Uuid): Timestamp

    @Insert
    suspend fun addTimestamps(timestamps: List<Timestamp>)
}