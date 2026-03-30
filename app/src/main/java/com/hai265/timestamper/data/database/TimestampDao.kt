package com.hai265.timestamper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * from videos ORDER BY lastEdited DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * from videos WHERE videoId =:id")
    suspend fun getVideoById(id: String): Video

    @Query("SELECT * from timestamps WHERE videoId = :id")
    fun getVideoTimestamps(id: String): Flow<List<Timestamp>>

    @Upsert
    suspend fun addVideo(video: Video)

    @Insert
    fun addTimestamp(timestamp: Timestamp)

    @Delete
    fun deleteVideo(video: Video)

    @Delete
    fun deleteTimestamp(timestamp: Timestamp)
}