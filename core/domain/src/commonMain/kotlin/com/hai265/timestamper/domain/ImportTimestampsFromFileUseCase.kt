package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.json.Json

class ImportTimestampsFromFileUseCase(
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke(source: Source) {
        val videoAndTimestamps =
            Json.decodeFromString<List<VideoWithTimestamps>>(source.readString())
        addTimestamps(videoAndTimestamps)
    }

    private suspend fun addTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
        videoRepository.importVideosWithTimestamps(
            videoWithTimestamps
        )
    }
}