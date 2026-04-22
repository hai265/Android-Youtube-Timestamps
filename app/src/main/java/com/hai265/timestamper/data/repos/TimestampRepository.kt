package com.hai265.timestamper.data.repos

import android.util.Log
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

    suspend fun addOrUpdateTimestamp(timestamp: Timestamp): Long {
        return withContext(Dispatchers.IO) {
            Log.d("TimestampRepository", "addOrUpdateTimestamp: ")
            //Room's upsert returns =-1 if existing timestamp is updated, returns id otherwise
            val timestampId = timestampDao.upsertTimestamp(timestamp)
            if (timestampId == -1L) {
                timestamp.id
            } else {
                timestampId
            }
        }
    }

    suspend fun deleteTimestamp(timestamp: Timestamp) {
        withContext(Dispatchers.IO) {
            timestampDao.deleteTimestamp(timestamp)
        }
    }

    suspend fun addEmptyTimestamp(videoId: String, duration: Duration): Long {
        return withContext(Dispatchers.IO) {
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