package com.hai265.timestamper.data.repos

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.TimestampDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration

class TimestampRepository @Inject constructor(
    private val timestampDao: TimestampDao
) {
    fun getTimestamps(videoId: String): Flow<List<Timestamp>> {
        return timestampDao.getTimestamps(videoId)
    }

    fun addOrUpdateTimestamp(timestamp: Timestamp) {
        timestampDao.upsertTimestamp(timestamp)
    }

    suspend fun deleteTimestamp(timestamp: Timestamp) {
        withContext(Dispatchers.IO) {
            timestampDao.deleteTimestamp(timestamp)
        }
    }

    suspend fun addEmptyTimestamp(videoId: String, duration: Duration) {
        withContext(Dispatchers.IO) {
            timestampDao.upsertTimestamp(
                Timestamp(
                    videoId = videoId,
                    timeMs = duration.inWholeMilliseconds,
                    description = "",
                )
            )
        }
    }
}