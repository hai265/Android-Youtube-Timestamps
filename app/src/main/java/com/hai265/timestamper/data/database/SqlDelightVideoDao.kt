package com.hai265.timestamper.data.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SqlDelightVideoDao(private val database: AppSqlDatabase) : VideoDao {
    private val queries = database.videosQueries

    override fun getAllVideos(): Flow<List<Video>> {
        return queries.getAllVideos().asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toVideo() }
        }
    }

    override suspend fun getAllVideosAndTimestamps(): List<VideoWithTimestamps> {
        return queries.getAllVideosWithTimestamps()
            .awaitAsList()
            .groupBy { it.id }
            .map { (_, rows) ->
                VideoWithTimestamps(
                    video = Video(
                        videoId = rows.first().video_id,
                        videoTitle = rows.first().video_title,
                        thumbnail = rows.first().thumbnail,
                        lastEdited = rows.first().last_edited,
                        lastPlayed = rows.first().last_played
                    ),
                    timestamps = rows.mapNotNull { row ->
                        row.id_?.let {
                            Timestamp(
                                id = it,
                                videoId = row.video_id ?: "",
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
            queries.getVideoById(id).awaitAsOneOrNull()?.toVideo()
        }

    //TODO: timestamp pass in duration
    override suspend fun updateLastPlayed(videoId: String, timestamp: Long) {
        withContext(Dispatchers.IO) {
            queries.updateLastPlayed(timestamp.milliseconds, videoId)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addVideo(video: Video) {
        queries.addVideo(
            id = Uuid.random().toString(),
            video_id = video.videoId,
            video_title = video.videoTitle ?: "",
            thumbnail = video.thumbnail,
            last_edited = video.lastEdited,
            last_played = video.lastPlayed
        )
    }

    override suspend fun deleteVideo(id: Video) {
        queries.deleteVideo(id.videoId)

    }

    override suspend fun updateLastEdited(videoId: String, now: Instant) {
        queries.updateLastEdited(now, videoId)
    }
}

private fun Videos.toVideo(): Video {
    return Video(
        videoId = this.video_id,
        videoTitle = this.video_title,
        thumbnail = this.thumbnail,
        lastEdited = this.last_edited,
        lastPlayed = this.last_played
    )
}
