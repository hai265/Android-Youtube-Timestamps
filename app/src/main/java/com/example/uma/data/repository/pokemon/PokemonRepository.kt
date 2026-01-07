package com.example.uma.data.repository.pokemon

import com.example.uma.data.models.CharacterBasic
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getAllCharacters(page: Int): Flow<List<CharacterBasic>>
}