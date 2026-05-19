package com.hai265.timestamper.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Videos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class SqlDelightVideoDao(database: AppSqlDatabase) : VideoDao {
    private val queries = database.videosQueries

    override fun getAllVideos(): Flow<List<Video>> {
        return queries.getAllVideos().asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toVideo() }
        }
    }

    override suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoById(id: String): Video? =
        withContext(Dispatchers.IO) {
            queries.getVideoById(id).executeAsOneOrNull()?.toVideo()
        }

    override suspend fun updateLastPlayed(videoId: String, timestamp: Long) {
        withContext(Dispatchers.IO) {
            queries.updateLastPlayed(timestamp, videoId)
        }
    }

    override suspend fun addVideo(video: Video) {
        queries.addVideo(
            video_id = video.videoId,
            video_title = video.videoTitle ?: "",
            thumbnail = video.thumbnail,
            last_edited = video.lastEdited.epochSeconds,
            last_played = video.lastPlayed.inWholeMilliseconds
        )
    }

    override fun deleteVideo(id: Video) {
        queries.deleteVideo(id.videoId)
    }

    override fun updateLastEdited(videoId: String, now: Instant) {
        queries.updateLastEdited(now.epochSeconds, videoId)
    }
}

private fun Videos.toVideo(): Video {
    return Video(
        videoId = this.video_id,
        videoTitle = this.video_title,
        thumbnail = this.thumbnail,
        lastEdited = Instant.fromEpochMilliseconds(this.last_edited),
        lastPlayed = this.last_played.milliseconds
    )
}
