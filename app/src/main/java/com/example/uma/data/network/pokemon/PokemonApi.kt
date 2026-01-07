package com.example.uma.data.network.pokemon

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

// https://pokeapi.co/docs/v2#pokemon
private const val BASE_URL = "https://pokeapi.co/api/v2/"

interface PokemonApiService {
    @GET("pokemon?offset={offset}")
    suspend fun getAllPokemon(@Path(value="offset") offset: Int): PokemonResponse
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PokemonNetworkModule {
    companion object {
        @Provides
        fun providesUmaApiService(): PokemonApiService {
            val json = Json {
                ignoreUnknownKeys = true // required: API returns huge objects with extra fields
                coerceInputValues = true
            }
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PokemonApiService::class.java)
        }
    }
}
