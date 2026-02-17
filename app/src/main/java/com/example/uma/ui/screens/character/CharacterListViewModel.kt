package com.example.uma.ui.screens.character

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.repository.character.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CharacterListViewModel"

//TODO: Make this a sealed class so we can show blank screen, loading, normal screen
data class CharacterListState(
    val list: List<CharacterBasic> = emptyList(),
    val syncing: Boolean = false
)

/*
 TODO Features
 1. Allow user to filter, sort (ascending, descending, filter based on some criteria
*/
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
) : ViewModel() {
    val searchTextBoxState: TextFieldState = TextFieldState()

    private val _uiState = MutableStateFlow(CharacterListState())
    val uiState: StateFlow<CharacterListState> = _uiState.asStateFlow()

    init {
        @OptIn(FlowPreview::class)
        snapshotFlow { searchTextBoxState.text }
            .debounce(300L)
            .combine(getAllItems()) { searchText, items ->
                _uiState.update { currentState: CharacterListState ->
                    (currentState.copy(list = filterItems(searchText.toString(), items)))
                }
            }.launchIn(viewModelScope)
    }

    private fun getAllItems(): Flow<List<CharacterBasic>> = characterRepository.getAllCharacters()

    /**
     * The logic to filter the list based on a search term.
     * TODO: Make a comparator instead?
     */
    private fun filterItems(
        searchTerm: String,
        items: List<CharacterBasic>
    ): List<CharacterBasic> {
        //Trim leading spaces
        val trimmedSearchTerm = searchTerm.trimStart()
        if (trimmedSearchTerm.isEmpty()) {
            return items
        }
        return items.filter { it.name.contains(searchTerm, ignoreCase = true) }
    }

    // --- Public methods for the UI to call ---

    fun refreshList() {
        _uiState.update {
            it.copy(syncing = true)
        }
        viewModelScope.launch {
            characterRepository.sync()
            _uiState.update {
                it.copy(syncing = false)
            }
        }
    }

    fun onFavoriteCharacter(id: Int) {
        val currentFavoriteStatus = uiState.value.list.find { it.id == id }?.isFavorite ?: return
        viewModelScope.launch {
            characterRepository.setCharacterFavoriteStatus(id, !currentFavoriteStatus)
        }
    }
}