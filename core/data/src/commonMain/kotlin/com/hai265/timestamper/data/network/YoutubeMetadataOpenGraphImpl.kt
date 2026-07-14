package com.hai265.timestamper.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.io.IOException

class YoutubeMetadataOpenGraphImpl(
    private val httpClient: HttpClient,
) : YoutubeMetadataApiService {

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadataResult {
        return try {
            val response: HttpResponse = httpClient.get(videoUrl) {
                header("User-Agent", "Mozilla/5.0 (compatible; MetadataFetcher/1.0)")
            }

            if (!response.status.isSuccess()) {
                return YoutubeMetadataResult.HttpError(
                    statusCode = response.status.value,
                    message = "Watch page request failed: ${response.status.value} ${response.status.description}",
                )
            }

            val html = response.bodyAsText()
            val title = extractMetaContent(html, "og:title")
            val thumbnail = extractMetaContent(html, "og:image")

            if (title != null && thumbnail != null) {
                YoutubeMetadataResult.Success(
                    YoutubeMetadata(
                        title = unescapeHtml(title),
                        thumbnail = thumbnail,
                    )
                )
            } else {
                YoutubeMetadataResult.HttpError(
                    statusCode = response.status.value,
                    message = "Open Graph tags not found (video may be private or unavailable)",
                )
            }
        } catch (e: IOException) {
            YoutubeMetadataResult.NetworkError(e.message)
        }
    }

    private fun extractMetaContent(html: String, property: String): String? {
        // Handles both attribute orderings YouTube's markup has used.
        val patterns = listOf(
            Regex("""<meta\s+property="$property"\s+content="([^"]*)"""),
            Regex("""<meta\s+content="([^"]*)"\s+property="$property""""),
        )
        for (pattern in patterns) {
            pattern.find(html)?.let { return it.groupValues[1] }
        }
        return null
    }

    private fun unescapeHtml(s: String): String =
        s.replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
}