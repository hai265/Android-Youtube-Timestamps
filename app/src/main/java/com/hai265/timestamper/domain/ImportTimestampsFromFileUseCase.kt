package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.models.Backup
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import kotlinx.serialization.json.Json
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class ImportTimestampsFromFileUseCase @Inject constructor(
    val timestampRepo: TimestampRepository,
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend operator fun invoke(uri: Uri) {
        invokeJson(uri)
//        contentResolver.openInputStream(uri)?.use { inputStream ->
//            inputStream.bufferedReader().use { import(it.readText()) }
//        }
    }

    private suspend fun import(yamlString: String) {
        val backup = Yaml().decodeFromString(Backup.serializer(), yamlString)
        //TODO: Make add video and add timestamp one transaction
        backup.videos.forEach { video ->
            if (videoRepository.addVideo(video.videoId) != VideoResult.Success) //TODO: Handle error states
            {
                return@forEach
            }
            val timestamps: List<Timestamp> = video.timestamps.map {
                Timestamp(
                    id = it.id.toLong(), //In firebase / extention, id is string "video_id+id"
                    videoId = video.videoId,
                    time = it.seconds.seconds,
                    description = it.description
                )
            }
            timestampRepo.addTimestamps(timestamps)
        }
    }

    suspend fun invokeJson(uri: Uri) {
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

