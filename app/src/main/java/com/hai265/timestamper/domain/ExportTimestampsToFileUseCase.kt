package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.models.Info
import com.hai265.timestamper.data.models.VideoBackup
import com.hai265.timestamper.data.models.toTag
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.coroutines.flow.first
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject

class ExportTimestampsToFileUseCase @Inject constructor(
    val timestampRepo: TimestampRepository,
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend fun invoke(videoId: String, uri: Uri) {
        contentResolver.openOutputStream(uri)?.use { writer ->
            writer.write(timestampsToYaml(videoId).toByteArray())
        }
    }

    private suspend fun timestampsToYaml(videoId: String): String {
        val yaml = Yaml()
        val video = videoRepository.getVideoById(videoId)
        val timestamps = timestampRepo.getTimestamps(videoId).first()

        val videoBackup = VideoBackup(
            info = Info(
                lastUpdated = video.lastEdited.epochSeconds,
                title = video.videoTitle ?: "",
                videoId = videoId
            ),
            tags = timestamps.map { it.toTag() }
        )
        return yaml.encodeToString(VideoBackup.serializer(), videoBackup)
    }
}

