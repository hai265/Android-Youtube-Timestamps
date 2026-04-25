package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.models.YoutubeYaml
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class YamlToTimestampsUseCase @Inject constructor(
    private val timestampRepo: TimestampRepository,
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke(yaml: String) {
        val yaml = Yaml().decodeFromString(YoutubeYaml.serializer(), yaml)

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