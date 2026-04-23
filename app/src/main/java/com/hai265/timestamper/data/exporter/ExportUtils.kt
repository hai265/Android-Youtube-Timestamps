package com.hai265.timestamper.data.exporter

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.models.Info
import com.hai265.timestamper.data.models.Tag
import com.hai265.timestamper.data.models.YoutubeYaml
import net.mamoe.yamlkt.Yaml
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class ExportUtils {
    private val yaml = Yaml()
    fun timestampsToYamlString(timestamps: List<Timestamp>): String {
        val youtubeYaml = YoutubeYaml(
            info = Info(
                lastUpdated = 0,
                title = "title",
                videoId = "0"
            ),
            tags = timestamps.map { it.toTag() }
        )
        return yaml.encodeToString(YoutubeYaml.serializer(), youtubeYaml)
    }

    //TODO: Write to file (use kotlin io)?
    fun timestmapsToYamlFile(timestamps: List<Timestamp>) {

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