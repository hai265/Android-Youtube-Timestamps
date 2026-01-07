package com.example.uma.data.repository.pokemon

import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.network.pokemon.NetworkPokemonCharacter
import com.example.uma.data.network.pokemon.PokemonApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor (
    val pokemonApi: PokemonApiService
): PokemonRepository {
    override fun getAllCharacters(): Flow<List<CharacterBasic>> = flow {
        val pokemons = pokemonApi.getAllPokemon().map {
            it.toBasicCharacter()
        }
        emit(pokemons)
    }

    private fun NetworkPokemonCharacter.toBasicCharacter(): CharacterBasic {
        return CharacterBasic(
            id = id,
            name = name,
            image = image,
            gameId = null,
            colorMain = "0",
            colorSub = "0",
            isFavorite = false,
        )
    }
}