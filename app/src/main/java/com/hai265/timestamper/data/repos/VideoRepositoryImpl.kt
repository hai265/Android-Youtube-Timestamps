package com.hai265.timestamper.data.repos

import androidx.room.withTransaction
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.TimestampDao
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.database.powersync.MyConnector
import com.hai265.timestamper.data.getYouTubeIdFromUrl
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import com.powersync.PowerSyncDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration

sealed interface VideoResult {
    data class Success(val videoId: String) : VideoResult
    data class VideoAlreadyExists(val videoId: String) : VideoResult

    data class InvalidUrl(val url: String) : VideoResult
    data class NetworkError(val errorMessage: String?) : VideoResult
}

class VideoRepositoryImpl @Inject constructor(
    val videoDao: VideoDao,
    val timestmapDao: TimestampDao,
    val youtubeMetadataApi: YoutubeMetadataApiService,
    val database: AppDatabase,
    private val powersyncDatabase: PowerSyncDatabase,
    private val connector: MyConnector,
    externalScope: CoroutineScope,
) : VideoRepository {

    init {
        externalScope.launch {
            powersyncDatabase.connect(connector)
        }
    }

    override fun getVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos()
    }

    override suspend fun getVideosWithTimestamps(): List<VideoWithTimestamps> {
        return videoDao.getAllVideosAndTimestamps()
    }

    override suspend fun getVideoById(id: String): Video? {
        return videoDao.getVideoById(id)
    }

    //Two errors can occur
    // 1. url is invalid
    // 2. video already added
    override suspend fun addVideo(url: String): VideoResult {
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

    override suspend fun importVideosWithTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
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

    override suspend fun deleteVideo(video: Video) {
        withContext(Dispatchers.IO) {
            videoDao.deleteVideo(video)
        }
    }

    //TODO: Optimize so I don't write to db every second
    //Performance profiling?
    override suspend fun updateVideoLastWatched(videoId: String, lastWatchedTimestamp: Duration) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastPlayed(videoId, lastWatchedTimestamp.inWholeMilliseconds)
        }
    }


    override suspend fun updateLastEdited(videoId: String) {
        withContext(Dispatchers.IO) {
            videoDao.updateLastEdited(videoId, Clock.System.now())
        }
    }

}