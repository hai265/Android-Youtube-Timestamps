package com.example.uma.data.repository.pokemon

import androidx.paging.PagingSource
import com.example.uma.data.models.CharacterBasic

interface PokemonRepository {
    fun getAllCharacters(): PagingSource<Int, CharacterBasic>
}