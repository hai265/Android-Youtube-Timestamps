package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.getYouTubeId
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.hai265.timestamper.data.models.VideoInfo
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration

sealed interface VideoResult {
    data object Success : VideoResult
    data class InvalidUrl(val url: String) : VideoResult
    data class NetworkError(val errorMessage: String?) : VideoResult
}

class VideoRepository @Inject constructor(
    val dao: VideoDao,
    val youtubeMetadataApi: YoutubeMetadataApiService,
) {
    fun getVideos(): Flow<List<Video>> {
        return dao.getAllVideos()
    }

    suspend fun getVideoById(id: String): Video {
        return dao.getVideoById(id)
    }

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    suspend fun addVideo(url: String): VideoResult {
        val videoId = getYouTubeId(url) ?: return VideoResult.InvalidUrl(url)

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
        dao.addVideo(
            Video(
                videoId = videoId,
                videoTitle = metadata.title,
                thumbnail = getYoutubeThumbnail(videoId),
                lastEdited = Duration.ZERO,
                lastPlayed = Duration.ZERO,
            )
        )
        return VideoResult.Success
    }

    suspend fun deleteVideo(video: Video) {
        withContext(Dispatchers.IO) {
            dao.deleteVideo(video)
        }
    }

    //TODO: Optimize so I don't write to db every second
    //Performance profiling?
    suspend fun updateVideoLastWatched(videoId: String, lastWatchedTimestamp: Duration) {
        withContext(Dispatchers.IO) {
            dao.updateLastPlayed(videoId, lastWatchedTimestamp.inWholeMilliseconds)
        }
    }


    fun getVideoInfo(videoId: String): VideoInfo {
        TODO("Return videoInfo from youtube-api-v3")
    }

}