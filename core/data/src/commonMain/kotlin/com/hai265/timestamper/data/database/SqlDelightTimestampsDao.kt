package com.hai265.timestamper.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.data.AppSqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SqlDelightTimestampsDao(private val database: AppSqlDatabase) : TimestampDao {
    val queries = database.timestampsQueries
    override fun getTimestamps(videoId: Uuid): Flow<List<Timestamp>> {
        return queries.getTimestmaps(videoId).asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toTimestamp() }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun upsertTimestamp(timestamp: Timestamp): Uuid {
        queries.upsertTimestamp(
            id = timestamp.id,
            video_id = timestamp.videoId,
            time = timestamp.time,
            description = timestamp.description,
        )
        return timestamp.id

    }

    override suspend fun deleteTimestamp(timestamp: Timestamp) {
        queries.deleteTimestamp(timestamp.id)
    }

    override fun getTimestampById(id: Uuid): Timestamp {
        //TODO: id string
        //TODO: await suspend
        return queries.getTimestampById(id).executeAsOne().toTimestamp()
    }

    override suspend fun addTimestamps(timestamps: List<Timestamp>) {
        database.transaction {
            timestamps.forEach { timestamp ->
                upsertTimestamp(timestamp)
            }
        }
    }
}

fun Timestamps.toTimestamp(): Timestamp {
    return Timestamp(
        id = this.id,
        videoId = this.video_id,
        time = this.time,
        description = this.description
    )
}