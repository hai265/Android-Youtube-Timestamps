package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.repos.AuthRepository
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportTimestampsFromFileUseCase @Inject constructor(
    val videoRepository: VideoRepository,
    val authRepository: AuthRepository,
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
        videoRepository.importVideosWithTimestamps(
            videoWithTimestamps
        )
    }
}

