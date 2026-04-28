package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.net.Uri
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.models.VideoBackup
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class ImportTimestampsFromFileUseCase @Inject constructor(
    val timestampRepo: TimestampRepository,
    val videoRepository: VideoRepository,
    val contentResolver: ContentResolver,
) {
    suspend fun invoke(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { import(it.readText()) }
        }
    }

    private suspend fun import(yamlString: String) {
        val yaml = Yaml().decodeFromString(VideoBackup.serializer(), yamlString)

        if (videoRepository.addVideo(yaml.info.videoId) != VideoResult.Success) //TODO: Handle error states
        {
            return
        }
        val timestamps: List<Timestamp> = yaml.tags.map {
            Timestamp(
                id = 0, //In firebase / extention, id is string "video_id+id"
                videoId = yaml.info.videoId,
                time = it.seconds.seconds,
                description = it.description
            )
        }
        timestampRepo.addTimestamps(timestamps)
    }
}

