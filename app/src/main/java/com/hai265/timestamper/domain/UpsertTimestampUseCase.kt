package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import javax.inject.Inject

class UpsertTimestampUseCase @Inject constructor(
    val timestampRepo: TimestampRepository,
    val videoRepository: VideoRepository,
) {
    suspend fun invoke(timestamp: Timestamp): Long {
        videoRepository.updateLastEdited(timestamp.videoId)
        return timestampRepo.addOrUpdateTimestamp(timestamp)
    }
}