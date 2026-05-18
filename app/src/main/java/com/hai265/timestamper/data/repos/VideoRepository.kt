package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoWithTimestamps
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface VideoRepository {
    fun getVideos(): Flow<List<Video>>

    suspend fun getVideosWithTimestamps(): List<VideoWithTimestamps>

    suspend fun getVideoById(id: String): Video?

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    suspend fun addVideo(url: String): VideoResult

    suspend fun importVideosWithTimestamps(videoWithTimestamps: List<VideoWithTimestamps>)

    suspend fun deleteVideo(video: Video)

    suspend fun updateLastEdited(videoId: String)

    suspend fun updateVideoLastWatched(videoId: String, lastWatchedTimestamp: Duration) {}

}