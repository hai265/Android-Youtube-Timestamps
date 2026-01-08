package com.example.uma.ui.screens.character.pokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.repository.pokemon.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "CharacterListViewModel"
private const val ITEMS_PER_PAGE = 30

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) : ViewModel() {

    val items: Flow<PagingData<CharacterBasic>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = {pokemonRepository.getAllCharacters()}
    ).flow
        .cachedIn(viewModelScope)

}