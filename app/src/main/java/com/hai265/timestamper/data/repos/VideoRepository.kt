package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.TimestampDao
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.getYouTubeIdFromUrl
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi

sealed interface VideoResult {
    data class Success(val videoId: String) : VideoResult
    data class VideoAlreadyExists(val videoId: String) : VideoResult

    data class InvalidUrl(val url: String) : VideoResult
    data class NetworkError(val errorMessage: String?) : VideoResult
}

@Singleton
class VideoRepository @Inject constructor(
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

    suspend fun getVideoById(id: String): Video? {
        return videoDao.getVideoById(id)
    }

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    @OptIn(ExperimentalUuidApi::class)
    suspend fun addVideo(url: String): VideoResult {
        val videoId = getYouTubeIdFromUrl(url) ?: return VideoResult.InvalidUrl(url)
        if (getVideoById(videoId) != null) {
            return VideoResult.VideoAlreadyExists(videoId)
        }
        val metadata = try {
            youtubeMetadataApi.getYoutubeMetadata(url)
        } catch (e: IOException) {
            return VideoResult.NetworkError(e.message)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                400 -> "Video Doesn't Exist"
                403 -> "Video Is Private"
                else -> e.message
            }
            return VideoResult.NetworkError(message)
        }

        videoDao.addVideo(
            Video(
                videoId = videoId,
                videoTitle = metadata.title,
                thumbnail = metadata.thumbnail,
                lastEdited = Clock.System.now(),
                lastPlayed = Duration.ZERO,
            )
        )
        return VideoResult.Success(videoId)
    }

    suspend fun importVideosWithTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
        //TODO: one transaction
        videoWithTimestamps.forEach { (video, timestamps) ->
            if (videoDao.getVideoById(video.videoId) == null) {
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
    suspend fun updateVideoLastWatched(videoId: String, lastWatchedTimestamp: Duration) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastPlayed(videoId, lastWatchedTimestamp.inWholeMilliseconds)
        }
    }


    suspend fun updateLastEdited(videoId: String) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastEdited(videoId, Clock.System.now())
        }
    }

}