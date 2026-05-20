package com.hai265.timestamper.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Timestamps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SqlDelightTimestampsDao(private val database: AppSqlDatabase) : TimestampDao {
    val queries = database.timestampsQueries
    override fun getTimestamps(videoId: String): Flow<List<Timestamp>> {
        return queries.getTimestmaps(videoId).asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toTimestamp() }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun upsertTimestamp(timestamp: Timestamp): Long {
        queries.upsertTimestamp(
            id = Uuid.random().toString(),
            video_id = timestamp.videoId,
            time = timestamp.time.inWholeMilliseconds,
            description = timestamp.description,
        )
        return 0L

    }

    override suspend fun deleteTimestamp(timestamp: Timestamp) {
        TODO("")
    }

    override fun getTimestampById(id: Long): Timestamp {
        //TODO: id string
        //TODO: await suspend
        return queries.getTimestampById(id.toString()).executeAsOne().toTimestamp()
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
        id = 0L,
        videoId = this.video_id,
        time = this.time.milliseconds,
        description = this.description
    )
}