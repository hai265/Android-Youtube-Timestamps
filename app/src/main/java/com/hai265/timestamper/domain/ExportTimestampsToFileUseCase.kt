package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.serialization.json.Json

class ExportTimestampsToFileUseCase(
    private val videoRepository: VideoRepository,
    private val contentResolver: ContentResolver
) {

    suspend operator fun invoke(uri: Uri): String {
        contentResolver.openOutputStream(uri)?.use { writer ->
            writer.write(getVideosAndTimestampsAsJsonString().toByteArray())
        }
        return getVideosAndTimestampsAsJsonString()
    }

    private suspend fun getVideosAndTimestampsAsJsonString(): String {
        val videosAndTimestamps = videoRepository.getVideosWithTimestamps()
        return Json.encodeToString(videosAndTimestamps)
    }
}

