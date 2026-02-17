package com.example.uma.ui.screens.supportcard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uma.domain.GetSupportCardsWithCharacterNameUseCase
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

private const val TAG = "SupportCardListViewModel"
data class SupportCardListState(
    val list : List<SupportCardListItem> = emptyList(),
    val syncing: Boolean = false
)

/*
 TODO Features
 1. Allow user to filter, sort (ascending, descending, filter based on some criteria
*/
@HiltViewModel
class SupportCardListViewModel @Inject constructor(
    private val getSupportCardsWithCharacterNameUseCase: GetSupportCardsWithCharacterNameUseCase
) : ViewModel() {
    // This is flow to eventually support sorting, etc
    val searchTextBoxState: TextFieldState = TextFieldState()

    private val _uiState = MutableStateFlow(SupportCardListState())
    val uiState: StateFlow<SupportCardListState> = _uiState.asStateFlow()

    init {
        @OptIn(FlowPreview::class)
        snapshotFlow { searchTextBoxState.text }
            .debounce(300L)
            .combine(getAllItems()) { searchText, items ->
                _uiState.update { currentState: SupportCardListState ->
                    (currentState.copy(list = filterItems(searchText.toString(), items)))
                }
            }.launchIn(viewModelScope)
    }

    private fun getAllItems(): Flow<List<SupportCardListItem>> =
        getSupportCardsWithCharacterNameUseCase.invoke()

    private fun filterItems(
        searchTerm: String,
        items: List<SupportCardListItem>
    ): List<SupportCardListItem> {
        //Trim leading spaces
        val trimmedSearchTerm = searchTerm.trimStart()
        if (trimmedSearchTerm.isEmpty()) {
            return items
        }
        return items.filter { it.characterName.contains(searchTerm, ignoreCase = true) }
    }

    private suspend fun syncData() {
        getSupportCardsWithCharacterNameUseCase.sync()
    }

    // --- Public methods for the UI to call ---

    fun refreshList() {
        _uiState.update {
            it.copy(syncing = true)
        }
        viewModelScope.launch {
            syncData()
            _uiState.update {
                it.copy(syncing = false)
            }
        }
    }
}