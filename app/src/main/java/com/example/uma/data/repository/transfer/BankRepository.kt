package com.example.uma.data.repository.transfer

import com.example.uma.data.models.Bank
import com.example.uma.data.models.Money
import kotlinx.coroutines.delay
import javax.inject.Inject

sealed class TransferResult {
    data object Success : TransferResult()
    data object InsufficientFunds : TransferResult()
    data object ExternalTransferNotAllowed : TransferResult()
    data class UnknownError(val message: String) : TransferResult()
}

class BankRepository @Inject constructor() {
    suspend fun getAccounts(): List<Bank> {
        return listOf(
            Bank(
                id = 1,
                name = "Chime",
                isExternal = false,
                type = "Checking",
                balance = Money("100", "USD"),
                last4Account = "1234",
                image = "img"
            ),
            Bank(
                id = 2,
                name = "Chase",
                isExternal = true,
                type = "Checking",
                balance = Money("100", "USD"),
                last4Account = "5678",
                image = "img"
            ),
            Bank(
                id = 3,
                name = "Capital One",
                isExternal = true,
                type = "Checking",
                balance = Money("100", "USD"),
                last4Account = "1643",
                image = "img"
            )
        )
    }

    suspend fun transfer(sourceId: Int, targetId: Int, transferAmount: Int): TransferResult {
        //mock success
        delay(1000)
        return TransferResult.Success
    }
}