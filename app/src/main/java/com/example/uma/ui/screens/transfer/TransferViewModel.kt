package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uma.data.models.Bank
import com.example.uma.data.repository.transfer.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TransferState(
    val list: List<Bank> = emptyList(),
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

    fun fetchAccounts() {
        viewModelScope.launch {
            val accounts = bankRepository.getAccounts()
            _uiState.update { it.copy(list = accounts) }
        }
    }
}