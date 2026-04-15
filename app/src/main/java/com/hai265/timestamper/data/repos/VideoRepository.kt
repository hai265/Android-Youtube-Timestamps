package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.getYouTubeId
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.hai265.timestamper.data.models.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration

sealed interface VideoResult {
    data object Success : VideoResult
    data class InvalidUrl(val url: String) : VideoResult
}

class VideoRepository @Inject constructor(
    val dao: VideoDao
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
        /*TODO: Get title by constructing url:
        https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
        Source: https://abdus.dev/posts/youtube-oembed/
        */
        val videoId = getYouTubeId(url) ?: return VideoResult.InvalidUrl(url)

        dao.addVideo(
            Video(
                videoId = videoId,
                videoTitle = null,
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