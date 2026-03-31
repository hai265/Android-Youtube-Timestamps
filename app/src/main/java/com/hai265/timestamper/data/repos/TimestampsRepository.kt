package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.getYouTubeId
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.hai265.timestamper.data.models.VideoInfo
import com.hai265.timestamper.ui.fakes.fakeVideo1
import com.hai265.timestamper.ui.fakes.fakeVideo2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface VideoResult {
    data object Success : VideoResult
    data class InvalidUrl(val url: String) : VideoResult
}

class TimestampsRepository @Inject constructor(
    val dao: VideoDao
) {
    fun getVideos(): Flow<List<Video>> {
//        return flow {
//            emit(fakeVideoList)
//        }
        return dao.getAllVideos()
    }

    suspend fun getVideoById(id: String): Video {
        if (id == fakeVideo1.videoId) return fakeVideo1
        else if (id == fakeVideo2.videoId) return fakeVideo2
        return dao.getVideoById(id)
    }

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    suspend fun addVideo(url: String): VideoResult {
        /*TODO: I can get title two ways:
            1. Try to see if android-youtube-player exposes something to get youtube title (update these fields when video page entered)
            2. Use yt-dlp to get title (local bundle or backend)
        */
        val videoId = getYouTubeId(url) ?: return VideoResult.InvalidUrl(url)

        dao.addVideo(
            Video(
                videoId = videoId,
                videoTitle = null,
                thumbnail = getYoutubeThumbnail(videoId),
                lastEdited = System.currentTimeMillis()
            )
        )
        return VideoResult.Success
    }

    suspend fun deleteVideo(video: Video) {
        withContext(Dispatchers.IO) {
            dao.deleteVideo(video)
        }
    }


    //TODO: Add delete video

    fun getVideoInfo(videoId: String): VideoInfo {
        TODO("Return videoInfo from youtube-api-v3")
    }

}