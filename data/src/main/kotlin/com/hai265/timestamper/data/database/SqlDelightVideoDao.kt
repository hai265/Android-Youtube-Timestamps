package com.hai265.timestamper.data.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.AppSqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
                        id = rows.first().id,
                        youtubeId = rows.first().youtube_id,
                        videoTitle = rows.first().video_title,
                        thumbnail = rows.first().thumbnail,
                        lastEdited = rows.first().last_edited,
                        lastPlayed = rows.first().last_played
                    ),
                    timestamps = rows.mapNotNull { row ->
                        val id = row.id_ ?: return@mapNotNull null
                        val videoId = row.video_id ?: return@mapNotNull null
                        val time = row.time ?: return@mapNotNull null
                        val description = row.description ?: return@mapNotNull null
                        Timestamp(
                            id = id,
                            videoId = videoId,
                            time = time,
                            description = description,
                        )
                    }
                )
            }
    }

    override suspend fun getVideoByYoutubeId(youtubeId: String): Video? =
        withContext(Dispatchers.IO) {
            queries.getVideoByYoutubeId(youtubeId).awaitAsOneOrNull()?.toVideo()
        }

    //TODO: timestamp pass in duration
    override suspend fun updateLastPlayed(videoId: Uuid, timestamp: Long) {
        withContext(Dispatchers.IO) {
            queries.updateLastPlayed(timestamp.milliseconds, videoId)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addVideo(video: Video) {
        queries.addVideo(
            id = video.id,
            youtube_id = video.youtubeId,
            video_title = video.videoTitle ?: "",
            thumbnail = video.thumbnail,
            last_edited = video.lastEdited,
            last_played = video.lastPlayed
        )
    }

    override suspend fun deleteVideo(video: Video) {
        queries.deleteVideo(video.id)

    }

    override suspend fun updateLastEdited(videoId: Uuid, now: Instant) {
        queries.updateLastEdited(now, videoId)
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun Videos.toVideo(): Video {
    return Video(
        id = this.id,
        youtubeId = this.youtube_id,
        videoTitle = this.video_title,
        thumbnail = this.thumbnail,
        lastEdited = this.last_edited,
        lastPlayed = this.last_played
    )
}
