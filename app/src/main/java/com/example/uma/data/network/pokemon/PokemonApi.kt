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

private const val BASE_URL = "https://pokeapi.co/api/v2"

interface PokemonApiService {
    @GET("character/info")
    suspend fun getAllPokemon(): List<NetworkPokemonCharacter>
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
