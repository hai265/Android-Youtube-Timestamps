package com.example.uma.data.network.pokemon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPokemonCharacter(
    val id: Int,
    val name: String,
    @SerialName("front_default")
    val image: String,
)
