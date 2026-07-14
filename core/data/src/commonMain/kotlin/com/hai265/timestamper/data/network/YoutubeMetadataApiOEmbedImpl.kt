package com.hai265.timestamper.data.network

import com.hai265.timestamper.data.getYouTubeIdFromUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import kotlinx.io.IOException

//test url: https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
class YoutubeMetadataApiOEmbedImpl(private val httpClient: HttpClient) :
    YoutubeMetadataApiService {

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadataResult {
        return try {
            val response = httpClient.get("oembed") {
                url {
                    parameters.append("url", videoUrl)
                }
            }
            when (response.status) {
                HttpStatusCode.OK ->
                    returnYoutubeMetadata(
                        videoUrl,
                        response.body()
                    )
                //Http 401 oembed not allowed
                else -> YoutubeMetadataResult.HttpError(
                    response.status.value,
                    response.status.description
                )
            }
        } catch (e: IOException) {
            YoutubeMetadataResult.NetworkError(e.message)
        }
    }

    private suspend fun returnYoutubeMetadata(
        youtubeUrl: String,
        metadata: YoutubeMetadata
    ): YoutubeMetadataResult {
        return getYouTubeIdFromUrl(youtubeUrl)?.let {
            //TODO: Move thumbnail override to Api Impl?
            val thumbnailUrl = getValidYoutubeThumbnail(it)
            YoutubeMetadataResult.Success(
                metadata.copy(
                    thumbnail = thumbnailUrl
                )
            )
        } ?: YoutubeMetadataResult.NetworkError("Youtube url empty")
    }

    suspend fun getValidYoutubeThumbnail(
        videoId: String
    ): String {
        for (quality in THUMBNAIL_QUALITIES) {
            val url = "https://img.youtube.com/vi/$videoId/$quality"
            if (thumbnailExists(url)) {
                return url
            }
        }
        // last resort fallback, always exists
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    private suspend fun thumbnailExists(url: String): Boolean {
        return try {
            val response = httpClient.get(url)
            if (response.status != HttpStatusCode.OK) return false

            val contentLength = response.contentLength()

            val size = contentLength ?: response.body<ByteArray>().size.toLong()

            size > MIN_VALID_THUMBNAIL_BYTES
        } catch (e: IOException) {
            false
        }
    }

    companion object {
        private val THUMBNAIL_QUALITIES = listOf(
            "maxresdefault.jpg",
            "mqdefault.jpg",
        )

        private const val MIN_VALID_THUMBNAIL_BYTES = 5000L
    }
}