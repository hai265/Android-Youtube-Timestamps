package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.repository.transfer.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


data class TransferState(
    val list: List<CharacterBasic> = emptyList(),
    val canTransfer: Boolean = false
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val bankRepository: BankRepository,
) : ViewModel() {


    val textFieldState: TextFieldState = TextFieldState("0.00")
    private val _uiState = MutableStateFlow(TransferState())
    val uiState: StateFlow<TransferState> = _uiState.asStateFlow()

    fun onTransfer() {

    }
}