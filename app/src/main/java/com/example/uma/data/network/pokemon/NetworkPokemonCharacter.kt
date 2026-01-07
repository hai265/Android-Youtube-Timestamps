package com.example.uma.data.network.pokemon

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPokemonCharacter(
    val name: String,
    val url: String,
)
