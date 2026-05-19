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
        return database.videoQueries.getAllVideos().asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toVideo() }
        }
    }

    override suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoById(id: String): Video? {
        TODO("Not yet implemented")
    }

    override suspend fun updateLastPlayed(videoId: String, timestamp: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun addVideo(video: Video) {
        database.videoQueries.addVideo()
    }

    override fun deleteVideo(id: Video) {
        TODO("Not yet implemented")
    }

    override fun updateLastEdited(videoId: String, now: Instant) {
        TODO("Not yet implemented")
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
