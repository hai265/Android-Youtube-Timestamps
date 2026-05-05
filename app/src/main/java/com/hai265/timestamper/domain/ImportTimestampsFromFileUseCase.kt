package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportTimestampsFromFileUseCase @Inject constructor(
    val timestampRepo: TimestampRepository,
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend operator fun invoke(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { jsonString ->
                val videoAndTimestamps =
                    Json.decodeFromString<List<VideoWithTimestamps>>(jsonString.readText())
                addTimestamps(videoAndTimestamps)
            }
        }
    }

    private suspend fun addTimestamps(videoWithTimestamps: List<VideoWithTimestamps>) {
        videoWithTimestamps.forEach { videoWithTimestamp ->
            if (videoRepository.addVideo(videoWithTimestamp.video.videoId) != VideoResult.Success) //TODO: Handle error states
            {
                return@forEach
            }
            timestampRepo.addTimestamps(videoWithTimestamp.timestamps)
        }
    }
}

