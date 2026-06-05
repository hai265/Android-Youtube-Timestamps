package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import javax.inject.Inject

class ExportTimestampsToFileUseCase @Inject constructor(
) {
    //TODO: move to constructor
    private val videoRepository: VideoRepository by inject(VideoRepository::class.java)
    private val contentResolver: ContentResolver by inject(ContentResolver::class.java)

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

