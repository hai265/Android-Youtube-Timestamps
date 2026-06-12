package com.hai265.timestamper.domain

import com.hai265.timestamper.data.repos.VideoRepository
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.writeString
import kotlinx.serialization.json.Json

//TODO: On ios use NSOutputStream.asSink() https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io/as-sink.html
class ExportTimestampsToFileUseCase(
    private val videoRepository: VideoRepository,
) {

    suspend operator fun invoke(sink: Sink) {
        val json = getVideosAndTimestampsAsJsonString()
        sink.buffered().use({ it.writeString(json) })
    }

    private suspend fun getVideosAndTimestampsAsJsonString(): String {
        val videosAndTimestamps = videoRepository.getVideosWithTimestamps()
        return Json.encodeToString(videosAndTimestamps)
    }
}