package com.hai265.timestamper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface VideoDao {
    @Query("SELECT * from videos ORDER BY last_edited DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Transaction
    @Query("SELECT * from videos")
    suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps>

    @Query("SELECT * from videos WHERE id =:id")
    suspend fun getVideoById(id: String): Video?

    @Query("UPDATE videos SET last_played = :timestamp WHERE id = :videoId")
    suspend fun updateLastPlayed(videoId: String, timestamp: Long)

    @Upsert
    suspend fun addVideo(video: Video)

    @Delete
    suspend fun deleteVideo(id: Video)

    @Query("UPDATE videos SET last_edited = :now WHERE id =:videoId")
    suspend fun updateLastEdited(videoId: String, now: Instant)
}

@Dao
interface TimestampDao {
    @Query("SELECT * from timestamps WHERE video_id = :videoId ORDER BY time ASC")
    fun getTimestamps(videoId: String): Flow<List<Timestamp>>

    @Upsert
    suspend fun upsertTimestamp(timestamp: Timestamp): Long

    @Delete
    suspend fun deleteTimestamp(timestamp: Timestamp)

    @Query("SELECT * from timestamps WHERE id = :id")
    fun getTimestampById(id: Long): Timestamp

    @Insert
    suspend fun addTimestamps(timestamps: List<Timestamp>)
}