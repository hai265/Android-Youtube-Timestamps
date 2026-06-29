package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ImportTimestampsFromFileUseCase(
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke(source: Source) {
        val videoAndTimestamps = try {
            Json.decodeFromString<List<VideoWithTimestamps>>(source.readString())
        } catch (e: SerializationException) {
            throw InvalidImportFileException("File is not correct JSON", e)
        }
        addTimestamps(videoAndTimestamps)
    }

    private suspend fun addTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
        videoRepository.importVideosWithTimestamps(
            videoWithTimestamps
        )
    }
}