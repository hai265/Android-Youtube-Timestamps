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
    @Query("SELECT * from videos ORDER BY lastEdited DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Transaction
    @Query("SELECT * from videos")
    suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps>

    @Query("SELECT * from videos WHERE videoId =:id")
    suspend fun getVideoById(id: String): Video

    @Query("UPDATE videos SET lastPlayed = :timestamp WHERE videoId = :videoId")
    suspend fun updateLastPlayed(videoId: String, timestamp: Long)

    @Upsert
    suspend fun addVideo(video: Video)

    @Delete
    fun deleteVideo(id: Video)

    @Query("UPDATE videos SET lastEdited = :now WHERE videoId =:videoId")
    fun updateLastEdited(videoId: String, now: Instant)
}

@Dao
interface TimestampDao {
    @Query("SELECT * from timestamps WHERE videoId = :videoId ORDER BY time ASC")
    fun getTimestamps(videoId: String): Flow<List<Timestamp>>

    @Upsert
    fun upsertTimestamp(timestamp: Timestamp): Long

    @Delete
    fun deleteTimestamp(timestamp: Timestamp)

    @Query("SELECT * from timestamps WHERE id = :id")
    fun getTimestampById(id: Long): Timestamp

    @Insert
    fun addTimestamps(timestamps: List<Timestamp>)
}