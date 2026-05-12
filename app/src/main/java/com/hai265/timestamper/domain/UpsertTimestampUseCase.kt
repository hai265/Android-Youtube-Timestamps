package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import javax.inject.Inject

class UpsertTimestampUseCase @Inject constructor(
    private val timestampRepo: TimestampRepository,
    private val videoRepository: VideoRepository,
    private val externalScope: CoroutineScope,
) {
    suspend fun invoke(timestamp: Timestamp): Long {
        videoRepository.updateLastEdited(timestamp.videoId)
        return timestampRepo.addOrUpdateTimestamp(timestamp)
    }

    suspend fun invokeExternalScope(timestamp: Timestamp): Long {
        return externalScope.async {
            videoRepository.updateLastEdited(timestamp.videoId)
            timestampRepo.addOrUpdateTimestamp(timestamp)
        }.await()
    }
}