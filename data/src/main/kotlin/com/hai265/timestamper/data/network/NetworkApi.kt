package com.hai265.timestamper.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.koin.dsl.module

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

internal val networkModule = module {
    single<OkHttpClient> {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single<YoutubeMetadataApiService> {
        YoutubeMetadataApiServiceImplKtor(get())
    }

    single {
        HttpClient(OkHttp) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
            install(DefaultRequest) {
                url(BASE_URL)
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                }
                )
            }
        }
    }
}