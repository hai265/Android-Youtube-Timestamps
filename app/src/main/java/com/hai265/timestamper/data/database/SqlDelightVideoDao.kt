package com.hai265.timestamper.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Videos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class SqlDelightVideoDao(private val database: AppSqlDatabase) : VideoDao {
    private val queries = database.videosQueries

    override fun getAllVideos(): Flow<List<Video>> {
        return queries.getAllVideos().asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toVideo() }
        }
    }

    override suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps> {
        return queries.getAllVideosWithTimestamps()
            .executeAsList()
            .groupBy { it.video_id }
            .map { (_, rows) ->
                VideoWithTimestamps(
                    video = Video(
                        id = rows.first().video_id,
                        videoTitle = rows.first().video_title,
                        thumbnail = rows.first().thumbnail,
                        lastEdited = Instant.fromEpochMilliseconds(rows.first().last_edited),
                        lastPlayed = rows.first().last_played.milliseconds
                    ),
                    timestamps = rows.mapNotNull { row ->
                        row.id?.let {
                            Timestamp(
                                id = it,
                                videoId = row.video_id,
                                time = row.time?.milliseconds ?: Duration.ZERO,
                                description = row.description ?: ""
                            )
                        }
                    }
                )
            }
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
            video_id = video.id,
            video_title = video.videoTitle ?: "",
            thumbnail = video.thumbnail,
            last_edited = video.lastEdited.epochSeconds,
            last_played = video.lastPlayed.inWholeMilliseconds
        )
    }

    override fun deleteVideo(id: Video) {
        queries.deleteVideo(id.id)
    }

    override fun updateLastEdited(videoId: String, now: Instant) {
        queries.updateLastEdited(now.epochSeconds, videoId)
    }
}

private fun Videos.toVideo(): Video {
    return Video(
        id = this.video_id,
        videoTitle = this.video_title,
        thumbnail = this.thumbnail,
        lastEdited = Instant.fromEpochMilliseconds(this.last_edited),
        lastPlayed = this.last_played.milliseconds
    )
}
