package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//TODO: Implement w/ room database (also add video button)
class TimestampsRepository @Inject constructor(
    val dao: VideoDao
) {
    fun getVideos(): Flow<List<Video>> {
        return dao.getAllVideos()
    }

    suspend fun getVideoById(id: String): Video {
        return dao.getVideoById(id)
    }

}