package com.hai265.timestamper.data.repos

import androidx.room.withTransaction
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.TimestampDao
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.hai265.timestamper.data.models.VideoInfo
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration

sealed interface VideoResult {
    data object Success : VideoResult
    object VideoAlreadyExists : VideoResult

    data class InvalidUrl(val url: String) : VideoResult
    data class NetworkError(val errorMessage: String?) : VideoResult
}

class VideoRepository @Inject constructor(
    val videoDao: VideoDao,
    val timestmapDao: TimestampDao,
    val youtubeMetadataApi: YoutubeMetadataApiService,
    val database: AppDatabase,
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
    suspend fun addVideo(videoId: String): VideoResult {
        val metadata = try {
            youtubeMetadataApi.getYoutubeMetadata("https://www.youtube.com/watch?v=$videoId")
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

        if (videoDao.getVideoById(videoId) != null) {
            return VideoResult.VideoAlreadyExists
        }
        videoDao.addVideo(
            Video(
                videoId = videoId,
                videoTitle = metadata.title,
                thumbnail = getYoutubeThumbnail(videoId),
                lastEdited = Clock.System.now(),
                lastPlayed = Duration.ZERO,
            )
        )
        return VideoResult.Success
    }

    suspend fun addVideoWithTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
        database.withTransaction {
            videoWithTimestamps.forEach { (video, timestamps) ->
                if (videoDao.getVideoById(video.videoId) == null) {
                    videoDao.addVideo(video)
                }
                timestamps.forEach {
                    timestmapDao.upsertTimestamp(it)
                }
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


    fun getVideoInfo(videoId: String): VideoInfo {
        TODO("Return videoInfo from youtube-api-v3")
    }

    suspend fun updateLastEdited(videoId: String) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastEdited(videoId, Clock.System.now())
        }
    }

}