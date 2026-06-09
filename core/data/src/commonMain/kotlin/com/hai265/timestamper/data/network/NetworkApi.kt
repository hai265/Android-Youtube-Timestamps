package com.hai265.timestamper.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//test url: https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
private const val BASE_URL = "https://www.youtube.com/"

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

class YoutubeMetadataApiServiceImplKtor(private val httpClient: HttpClient) :
    YoutubeMetadataApiService {

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadataResult {
        return try {
            val response = httpClient.get("oembed") {
                url {
                    parameters.append("url", videoUrl)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> YoutubeMetadataResult.Success(response.body())
                else -> YoutubeMetadataResult.HttpError(
                    response.status.value,
                    response.status.description
                )
            }
        } catch (e: IOException) {
            YoutubeMetadataResult.NetworkError(e.message)
        }
    }
}


@Serializable
data class YoutubeMetadata(
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnail: String,
)