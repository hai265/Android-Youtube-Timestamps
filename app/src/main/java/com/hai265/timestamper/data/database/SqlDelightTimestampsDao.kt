package com.hai265.timestamper.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Timestamps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

class SqlDelightTimestampsDao(private val database: AppSqlDatabase) : TimestampDao {
    val queries = database.timestampsQueries
    override fun getTimestamps(videoId: String): Flow<List<Timestamp>> {
        return queries.getTimestmaps(videoId).asFlow().mapToList(Dispatchers.IO).map {
            it.map { it.toTimestamp() }
        }
    }

    override fun upsertTimestamp(timestamp: Timestamp): Long {
        return if (timestamp.id == 0L) {
            queries.insertNewTimestamp(
                video_id = timestamp.videoId,
                time = timestamp.time.inWholeMilliseconds,
                description = timestamp.description,
            )
            queries.getLastInsertId().executeAsOne()
        } else {
            queries.upsertTimestamp(
                id = timestamp.id,
                video_id = timestamp.videoId,
                time = timestamp.time.inWholeMilliseconds,
                description = timestamp.description,
            )
            timestamp.id
        }

    }

    override fun deleteTimestamp(timestamp: Timestamp) {
        queries.deleteTimestamp(timestamp.id)
    }

    override fun getTimestampById(id: Long): Timestamp {
        return queries.getTimestampById(id).executeAsOne().toTimestamp()
    }

    override fun addTimestamps(timestamps: List<Timestamp>) {
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
        time = this.time.milliseconds,
        description = this.description
    )
}