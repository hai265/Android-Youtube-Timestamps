package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.TimestampDao
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.getYouTubeIdFromUrl
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface VideoResult {
    data class Success(val videoId: Uuid) : VideoResult
    data class VideoAlreadyExists(val videoId: Uuid) : VideoResult

    data class InvalidUrl(val url: String) : VideoResult
    data class NetworkError(val errorMessage: String?) : VideoResult
}

class VideoRepository(
    val videoDao: VideoDao,
    val timestmapDao: TimestampDao,
    val youtubeMetadataApi: YoutubeMetadataApiService,
) {

    fun getVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos()
    }

    suspend fun getVideosWithTimestamps(): List<VideoWithTimestamps> {
        return videoDao.getAllVideosAndTimestamps()
    }

    suspend fun getVideoByYoutubeId(id: String): Video? {
        return videoDao.getVideoByYoutubeId(id)
    }

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    @OptIn(ExperimentalUuidApi::class)
    suspend fun addVideo(url: String): VideoResult {
        val youtubeId = getYouTubeIdFromUrl(url) ?: return VideoResult.InvalidUrl(url)
        //return if video already exists
        getVideoByYoutubeId(youtubeId)?.id?.let { return VideoResult.VideoAlreadyExists(it) }
        val metadata = try {
            youtubeMetadataApi.getYoutubeMetadata(url)
        } catch (e: IOException) {
            return VideoResult.NetworkError(e.message)
        } catch (e: ClientRequestException) {
            val message = when (e.response.status) {
                HttpStatusCode.BadRequest -> "Video Doesn't Exist"
                HttpStatusCode.Forbidden -> "Video Is Private"
                else -> e.message
            }
            return VideoResult.NetworkError(message)
        }

        val newVideoId = Uuid.random()
        videoDao.addVideo(
            Video(
                id = newVideoId,
                youtubeId = youtubeId,
                videoTitle = metadata.title,
                thumbnail = getYoutubeThumbnail(youtubeId),
                lastEdited = Clock.System.now(),
                lastPlayed = Duration.ZERO,
            )
        )
        return VideoResult.Success(newVideoId)
    }

    suspend fun importVideosWithTimestamps(
        videoWithTimestamps: List<VideoWithTimestamps>
    ) {
        //TODO: one transaction
        videoWithTimestamps.forEach { (video, timestamps) ->
            if (videoDao.getVideoByYoutubeId(video.youtubeId) == null) {
                videoDao.addVideo(video)
            }
            timestamps.forEach {
                timestmapDao.upsertTimestamp(it)
            }
        }

    }

    suspend fun deleteVideo(video: Video) {
        withContext(Dispatchers.IO) {
            videoDao.deleteVideo(video)
        }
    }

    //TODO: Optimize so I don't write to db every second
    //Performance profiling?
    suspend fun updateVideoLastWatched(videoId: Uuid, lastWatchedTimestamp: Duration) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastPlayed(videoId, lastWatchedTimestamp.inWholeMilliseconds)
        }
    }


    suspend fun updateLastEdited(videoId: Uuid) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastEdited(videoId, Clock.System.now())
        }
    }

}