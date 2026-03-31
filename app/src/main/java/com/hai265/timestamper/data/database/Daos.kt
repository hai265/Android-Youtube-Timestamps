package com.hai265.timestamper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * from videos ORDER BY lastEdited DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * from videos WHERE videoId =:id")
    suspend fun getVideoById(id: String): Video

    @Upsert
    suspend fun addVideo(video: Video)

    @Delete
    fun deleteVideo(id: Video)
}

@Dao
interface TimestampDao {
    @Query("SELECT * from timestamps WHERE videoId = :id")
    fun getTimestamps(id: String): Flow<List<Timestamp>>

    @Upsert
    fun upsertTimestamp(timestamp: Timestamp)

    @Delete
    fun deleteTimestamp(timestamp: Timestamp)
}