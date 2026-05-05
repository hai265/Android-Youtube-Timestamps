package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExportTimestampsToFileUseCase @Inject constructor(
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend operator fun invoke(uri: Uri) {
        contentResolver.openOutputStream(uri)?.use { writer ->
            writer.write(getVideosAndTimestampsAsJsonString().toByteArray())
        }
    }

    private suspend fun getVideosAndTimestampsAsJsonString(): String {
        val videosAndTimestamps = videoRepository.getVideosWithTimestamps()
        return Json.encodeToString(videosAndTimestamps)
    }
}

