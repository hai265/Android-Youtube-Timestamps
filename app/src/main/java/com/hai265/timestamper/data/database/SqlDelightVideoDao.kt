package com.hai265.timestamper.data.database

import android.content.Context
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Videos
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class SqlDelightVideoDao @Inject constructor(@ApplicationContext context: Context) : VideoDao {
    val database = AppSqlDatabase(
        AndroidSqliteDriver(
            AppSqlDatabase.Schema, context, "app_database.db"
        )
    )

    override fun getAllVideos(): Flow<List<Video>> {
        return database.videosQueries.getAllVideos().asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toVideo() }
        }
    }

    override suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoById(id: String): Video? =
        withContext(Dispatchers.IO) {
            database.videosQueries.getVideoById(id).executeAsOneOrNull()?.toVideo()
        }

    override suspend fun updateLastPlayed(videoId: String, timestamp: Long) {
        withContext(Dispatchers.IO) {
            database.videosQueries.updateLastPlayed(timestamp, videoId)
        }
    }

    override suspend fun addVideo(video: Video) {
        database.videosQueries.addVideo(
            video_id = video.videoId,
            video_title = video.videoTitle ?: "",
            thumbnail = video.thumbnail,
            last_edited = video.lastEdited.epochSeconds,
            last_played = video.lastPlayed.inWholeMilliseconds
        )
    }

    override fun deleteVideo(id: Video) {
        database.videosQueries.deleteVideo(id.videoId)
    }

    override fun updateLastEdited(videoId: String, now: Instant) {
        database.videosQueries.updateLastEdited(now.epochSeconds, videoId)
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
