package com.example.uma.data.repository.pokemon

import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.network.pokemon.NetworkPokemonCharacter
import com.example.uma.data.network.pokemon.PokemonApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PokemonRepositoryImpl @Inject constructor(
    val pokemonApi: PokemonApiService
) : PokemonRepository {
    override fun getAllCharacters(page: Int): Flow<List<CharacterBasic>> = flow {
        val pokemons = pokemonApi.getAllPokemon(offset = 0, limit = 20).results.map {
            it.toBasicCharacter()
        }
        emit(pokemons)
    }

    private fun NetworkPokemonCharacter.toBasicCharacter(): CharacterBasic {
        val id = this.extractId()
        return CharacterBasic(
            id = id,
            name = name,
            image = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
            gameId = id,
            colorMain = "0",
            colorSub = "0",
            isFavorite = false,
        )
    }

    private fun NetworkPokemonCharacter.extractId(): Int {
        return url.trimEnd('/').substringAfterLast('/').toInt()
    }
}