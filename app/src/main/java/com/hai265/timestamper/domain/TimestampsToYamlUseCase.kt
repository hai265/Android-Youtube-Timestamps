package com.hai265.timestamper.domain

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.models.Info
import com.hai265.timestamper.data.models.Tag
import com.hai265.timestamper.data.models.YoutubeYaml
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.coroutines.flow.first
import net.mamoe.yamlkt.Yaml
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class TimestampsToYamlStringUseCase @Inject constructor(
    private val timestampRepo: TimestampRepository,
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke(videoId: String): String {
        val yaml = Yaml()
        val video = videoRepository.getVideoById(videoId)
        val timestamps = timestampRepo.getTimestamps(videoId).first()

        val youtubeYaml = YoutubeYaml(
            info = Info(
                lastUpdated = video.lastEdited.inWholeMilliseconds,
                title = video.videoTitle ?: "",
                videoId = videoId
            ),
            tags = timestamps.map { it.toTag() }
        )
        return yaml.encodeToString(YoutubeYaml.serializer(), youtubeYaml)
    }
}

@OptIn(ExperimentalTime::class)
fun Timestamp.toTag(): Tag {
    return Tag(
        description = this.description,
        id = this.id.toString(),
        seconds = Duration.convert(
            this.timeMs.toDouble(),
            DurationUnit.MILLISECONDS,
            DurationUnit.SECONDS
        )
    )
}