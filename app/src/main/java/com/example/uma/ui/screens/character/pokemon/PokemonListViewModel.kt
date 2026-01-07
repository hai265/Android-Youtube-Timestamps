package com.example.uma.ui.screens.character.pokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.repository.pokemon.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CharacterListViewModel"

//TODO: Make this a sealed class so we can show blank screen, loading, normal screen
data class CharacterListState(
    val list: List<CharacterBasic> = emptyList(),
    val syncing: Boolean = false
)

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokmeonRepository: PokemonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListState())
    val uiState: StateFlow<CharacterListState> = _uiState.asStateFlow()

    init {
        getCharacters()
    }

    fun getCharacters() {
        viewModelScope.launch {
            pokmeonRepository.getAllCharacters(0).collect {
                uiState.value.copy(list = it)
            }
        }
    }

}