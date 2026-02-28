package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
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
    val canTransfer: Boolean = false,
    val sourceBank: Bank? = null,
    val targetBank: Bank? = null,
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val bankRepository: BankRepository,
) : ViewModel() {


    val transferAmountState: TextFieldState = TextFieldState("100")
    private val _uiState = MutableStateFlow(TransferState())
    val uiState: StateFlow<TransferState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { transferAmountState.text }
                .collect {
                    updateCanTransfer()
                }
        }
    }


    fun onTransfer() {
        val sourceBank = _uiState.value.sourceBank
        val targetBank = _uiState.value.targetBank
        if (sourceBank == null || targetBank == null) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(canTransfer = false) }
            bankRepository.transfer(
                sourceBank.id,
                targetBank.id,
                transferAmountState.text.toString().toInt()
            )
            _uiState.update { it.copy(canTransfer = true) }
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            val accounts = bankRepository.getAccounts()
            _uiState.update { it.copy(list = accounts) }
        }
    }

    fun setSourceBank(bank: Bank) {
        _uiState.update { it.copy(sourceBank = bank) }
        updateCanTransfer()
    }

    fun setTargetBank(bank: Bank) {
        _uiState.update { it.copy(targetBank = bank) }
        updateCanTransfer()
    }

    private fun updateCanTransfer() {
        //Rules:
        //1.can't transfer between two external
        //2. can't transfer when balance exceeds source balance
        val targetBank = uiState.value.targetBank
        val sourceBank = uiState.value.sourceBank

        if (targetBank == null || sourceBank == null) {
            _uiState.update { it.copy(canTransfer = false) }
        } else if (targetBank.isExternal && sourceBank.isExternal) {
            _uiState.update { it.copy(canTransfer = false) }
        } else if (targetBank.balance.amount.toFloat() < transferAmountState.text.toString()
                .toFloat()
        ) {
            _uiState.update { it.copy(canTransfer = false) }
        } else {
            _uiState.update { it.copy(canTransfer = true) }
        }
    }
}