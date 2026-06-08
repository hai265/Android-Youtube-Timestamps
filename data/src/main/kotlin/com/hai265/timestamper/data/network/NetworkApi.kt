package com.hai265.timestamper.data.network

import com.hai265.timestamper.data.getYouTubeIdFromUrl
import com.hai265.timestamper.data.getYoutubeThumbnail
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

//test url: https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
private const val BASE_URL = "https://www.youtube.com/"

interface YoutubeMetadataApiService {
    suspend fun getYoutubeMetadata(
        videoUrl: String,
    ): YoutubeMetadata
}

class YoutubeMetadataApiServiceImpl private constructor(private val api: YoutubeMetadataApi) :
    YoutubeMetadataApiService {
    private interface YoutubeMetadataApi {
        @GET("oembed")
        suspend fun getYoutubeMetadata(
            @Query("url") videoUrl: String,
        ): YoutubeMetadata
    }

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadata {
        return api.getYoutubeMetadata(videoUrl).copy(
            thumbnail = getYoutubeThumbnail(
                getYouTubeIdFromUrl(videoUrl) ?: ""
            )
        )
    }

    companion object {
        fun create(retrofit: Retrofit): YoutubeMetadataApiServiceImpl {
            return YoutubeMetadataApiServiceImpl(retrofit.create(YoutubeMetadataApi::class.java))
        }
    }
}

class YoutubeMetadataApiServiceImplKtor(private val httpClient: HttpClient) :
    YoutubeMetadataApiService {

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadata {
        return httpClient.get("oembed") {
            url {
                parameters.append("url", videoUrl)
            }
        }.body()
    }
}


@Serializable
data class YoutubeMetadata(
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnail: String,
)

internal val networkModule = module {
    single<Retrofit> {
        val json = Json {
            ignoreUnknownKeys = true
        }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(get<OkHttpClient>())
            .build()
    }
    single<OkHttpClient> {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single<YoutubeMetadataApiService> {
//        YoutubeMetadataApiServiceImpl.create(get<Retrofit>())
        YoutubeMetadataApiServiceImplKtor(get())
    }

    single {
        HttpClient(io.ktor.client.engine.okhttp.OkHttp) {
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