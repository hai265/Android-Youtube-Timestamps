package com.hai265.timestamper.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface YoutubeMetadataApiService {
    suspend fun getYoutubeMetadata(
        videoUrl: String,
    ): YoutubeMetadataResult
}

sealed interface YoutubeMetadataResult {
    data class Success(val metadata: YoutubeMetadata) : YoutubeMetadataResult
    data class HttpError(val statusCode: Int, val message: String) : YoutubeMetadataResult
    data class NetworkError(val message: String?) : YoutubeMetadataResult
}


@Serializable
data class YoutubeMetadata(
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnail: String,
)