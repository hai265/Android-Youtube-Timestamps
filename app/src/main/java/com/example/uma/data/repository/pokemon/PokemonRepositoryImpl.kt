package com.example.uma.data.repository.pokemon

import javax.inject.Inject


class PokemonRepositoryImpl @Inject constructor(
    val pokemonPagingSource: PokemonPagingSource
) : PokemonRepository {
    override fun getAllCharacters() = pokemonPagingSource
}