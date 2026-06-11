package com.hai265.timestamper.data.repos

import co.touchlab.kermit.Logger
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.TimestampDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class TimestampRepository(
    private val timestampDao: TimestampDao
) {
    fun getTimestamps(videoId: Uuid): Flow<List<Timestamp>> {
        return timestampDao.getTimestamps(videoId)
    }

    suspend fun addOrUpdateTimestamp(timestamp: Timestamp): Uuid {
        return withContext(Dispatchers.IO) {
            Logger.d(tag = "TimestampRepository") { "addOrUpdateTimestamp: " }
            //Room's upsert returns =-1 if existing timestamp is updated, returns id otherwise
            val timestampId = timestampDao.upsertTimestamp(timestamp)
            timestampId
        }
    }

    suspend fun deleteTimestamp(timestamp: Timestamp) {
        withContext(Dispatchers.IO) {
            timestampDao.deleteTimestamp(timestamp)
        }
    }

}