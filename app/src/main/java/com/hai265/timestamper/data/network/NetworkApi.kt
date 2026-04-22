package com.hai265.timestamper.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton


//test url: https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
private const val BASE_URL = "https://www.youtube.com/"

private val retroJson = Json { ignoreUnknownKeys = true }

interface YoutubeMetadataApiService {
    @GET("oembed")
    suspend fun getYoutubeMetadata(
        @Query("url") videoUrl: String,
    ): YoutubeMetadata
}

@Serializable
data class YoutubeMetadata(
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnail: String,
)

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    companion object {
        @Singleton
        @Provides
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            val json = Json {
                ignoreUnknownKeys = true
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .client(okHttpClient)
                .build()
        }

        @Provides
        fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        @Provides
        fun providesYoutubeMetadataApiService(retrofit: Retrofit): YoutubeMetadataApiService {
            return retrofit.create(YoutubeMetadataApiService::class.java)
        }
    }
}