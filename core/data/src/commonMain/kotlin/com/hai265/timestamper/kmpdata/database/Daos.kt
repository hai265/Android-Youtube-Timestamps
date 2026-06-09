package com.hai265.timestamper.data.database

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface VideoDao {
    fun getAllVideos(): Flow<List<Video>>

    suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps>

    suspend fun getVideoByYoutubeId(youtubeId: String): Video?

    suspend fun updateLastPlayed(videoId: Uuid, timestamp: Long)

    suspend fun addVideo(video: Video)

    suspend fun deleteVideo(video: Video)

    suspend fun updateLastEdited(videoId: Uuid, now: Instant)
}

interface TimestampDao {
    fun getTimestamps(videoId: Uuid): Flow<List<Timestamp>>

    suspend fun upsertTimestamp(timestamp: Timestamp): Uuid

    suspend fun deleteTimestamp(timestamp: Timestamp)

    fun getTimestampById(id: Uuid): Timestamp

    suspend fun addTimestamps(timestamps: List<Timestamp>)
}