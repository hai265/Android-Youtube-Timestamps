package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uma.data.models.Bank
import com.example.uma.data.repository.transfer.BankRepository
import com.example.uma.data.repository.transfer.TransferResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private data class InternalTransferState(
    val list: List<Bank> = emptyList(),
    val canTransfer: Boolean = false,
    val sourceBank: Bank? = null,
    val targetBank: Bank? = null,
    val isLoading: Boolean = false
)

data class TransferState(
    val list: List<Bank> = emptyList(),
    val canTransfer: Boolean = false,
    val sourceBank: Bank? = null,
    val targetBank: Bank? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val bankRepository: BankRepository,
) : ViewModel() {


    val transferAmountState: TextFieldState = TextFieldState("100")
    private val _internalUiState = MutableStateFlow(TransferState())
    val uiState: StateFlow<TransferState> = combine(
        _internalUiState,
        snapshotFlow { transferAmountState.text } // React to text field changes
    ) { internalState, amountText ->
        val canTransfer = calculateCanTransfer(
            sourceBank = internalState.sourceBank,
            targetBank = internalState.targetBank,
            transferAmountString = amountText.toString()
        )
        // Return a new data class that includes the derived canTransfer
        TransferState(
            list = internalState.list,
            sourceBank = internalState.sourceBank,
            targetBank = internalState.targetBank,
            isLoading = internalState.isLoading,
            canTransfer = canTransfer
        )
    }.stateIn(
        viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000L),
        initialValue = TransferState(isLoading = true) // Initial value with isLoading true until data fetched
    )

    private val _events = Channel<TransferResult>()
    val events = _events.receiveAsFlow()


    fun onTransfer() {
        val sourceBank = _internalUiState.value.sourceBank
        val targetBank = _internalUiState.value.targetBank
        if (sourceBank == null || targetBank == null) {
            return
        }

        viewModelScope.launch {
            _internalUiState.update { it.copy(canTransfer = false, isLoading = true) }
                val transferResult = bankRepository.transfer(
                    sourceBank.id,
                    targetBank.id,
                    transferAmountState.text.toString().toInt()
                )
                _events.send(transferResult) // Emit success event

            _internalUiState.update { it.copy(canTransfer = true, isLoading = false) }
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            val accounts = bankRepository.getAccounts()
            _internalUiState.update { it.copy(list = accounts) }
        }
    }

    fun setSourceBank(bank: Bank) {
        _internalUiState.update { it.copy(sourceBank = bank) }
    }

    fun setTargetBank(bank: Bank) {
        _internalUiState.update { it.copy(targetBank = bank) }
    }

    private fun calculateCanTransfer(
        sourceBank: Bank?,
        targetBank: Bank?,
        transferAmountString: String
    ): Boolean {

        if (sourceBank == null || targetBank == null) {
            return false
        }

        if (sourceBank.isExternal && targetBank.isExternal) {
            return false
        }

        val transferAmount = transferAmountString.toFloatOrNull() ?: 0f // Handle invalid number gracefully
        if (transferAmount <= 0) { // Ensure amount is positive
            return false
        }

        if (sourceBank.balance.amount.toFloat() < transferAmount) {
            return false
        }

        return true
    }
}