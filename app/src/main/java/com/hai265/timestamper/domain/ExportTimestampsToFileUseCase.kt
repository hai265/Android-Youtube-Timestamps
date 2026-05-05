package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.models.Backup
import com.hai265.timestamper.data.models.TimestampBackup
import com.hai265.timestamper.data.models.VideoBackup
import com.hai265.timestamper.data.repos.VideoRepository
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject

class ExportTimestampsToFileUseCase @Inject constructor(
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend fun invoke(uri: Uri) {
        contentResolver.openOutputStream(uri)?.use { writer ->
            writer.write(timestampsToYaml().toByteArray())
        }
    }

    private suspend fun timestampsToYaml(): String {
        val yaml = Yaml()
        val videosAndTimestamps = videoRepository.getVideosWithTimestamps()

        val backup = Backup(videos = videosAndTimestamps.map {
            val video = it.video
            val timestamps = it.timestamps
            VideoBackup(
                lastUpdated = video.lastEdited.epochSeconds,
                title = video.videoTitle ?: "",
                videoId = video.videoId,
                timestamps = timestamps.map { timestamp ->
                    TimestampBackup(
                        description = timestamp.description,
                        id = timestamp.id.toString(),
                        seconds = timestamp.time.inWholeSeconds.toDouble(),
                    )
                }
            )
        })

        return yaml.encodeToString(Backup.serializer(), backup)
    }
}

