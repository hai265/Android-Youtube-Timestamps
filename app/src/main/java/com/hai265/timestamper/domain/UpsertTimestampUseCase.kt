package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.uuid.Uuid

class UpsertTimestampUseCase(
    private val timestampRepo: TimestampRepository,
    private val videoRepository: VideoRepository,
    private val externalScope: CoroutineScope,
) {
    suspend fun invokeExternalScope(timestamp: Timestamp): Uuid {
        return externalScope.async {
            videoRepository.updateLastEdited(timestamp.videoId)
            timestampRepo.addOrUpdateTimestamp(timestamp)
        }.await()
    }
}