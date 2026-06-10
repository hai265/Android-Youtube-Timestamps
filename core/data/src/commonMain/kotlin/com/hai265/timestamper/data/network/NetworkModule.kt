package com.hai265.timestamper.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

//test url: https://www.youtube.com/oembed?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DdQw4w9WgXcQ
private const val BASE_URL = "https://www.youtube.com/"


val networkModule = module {
//    single<OkHttpClient> {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//    }

    single<YoutubeMetadataApiService> {
        YoutubeMetadataApiServiceImplKtor(get())
    }

    single {
        HttpClient {
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