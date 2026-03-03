package com.example.uma.ui.screens.transfer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.uma.R
import com.example.uma.data.models.Bank
import com.example.uma.data.repository.transfer.TransferResult
import kotlinx.coroutines.flow.collectLatest


//Features:
// can't transfer between two externals
// can't transfer more than balance

//TODO: For now we can disable the button when they chooose two externals
//better: we don't allow them to choose between two external accounts
@Composable
fun TransferScreen(modifier: Modifier = Modifier) {
    val viewModel: TransferViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { // Use Unit as key to run once when the composable enters composition
        viewModel.events.collectLatest { transferResult ->
            val toastMessage = when(transferResult) {
                TransferResult.ExternalTransferNotAllowed -> "External not allowed"
                TransferResult.InsufficientFunds -> "Insuficient funds"
                TransferResult.SameAccountNotAllowed -> "Cannot transfer money between same account"
                TransferResult.Success -> "Transfer success"
                is TransferResult.UnknownError -> "Unkonwn error"
            }
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAccounts()
    }

    Content(
        viewModel.transferAmountState, { viewModel.onTransfer() },
        sourceAccounts = state.list,
        targetAccounts = state.list,
        canTransfer = state.canTransfer,
        sourceBank = state.sourceBank,
        targetBank = state.targetBank,
        setSourceBank = { viewModel.setSourceBank(it) },
        setTargetBank = { viewModel.setTargetBank(it) },
        modifier = modifier
    )
}

@Composable
private fun Content(
    textFieldState: TextFieldState,
    onTransfer: () -> Unit,
    canTransfer: Boolean,
    sourceAccounts: List<Bank>,
    targetAccounts: List<Bank>,
    sourceBank: Bank?,
    targetBank: Bank?,
    setSourceBank: (bank: Bank) -> Unit,
    setTargetBank: (bank: Bank) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(modifier = modifier) {
        TextField(state = textFieldState)
        LongBasicDropdownMenu(sourceAccounts, sourceBank, setSourceBank)
        LongBasicDropdownMenu(targetAccounts, targetBank, setTargetBank)
        Button(onClick = { onTransfer() }, enabled = canTransfer) {
            Text("Transfer")
        }
    }
}


@Composable
fun LongBasicDropdownMenu(
    bankList: List<Bank>,
    selectedBank: Bank?,
    onSelectBank: (bank: Bank) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.clickable { expanded = !expanded }) {
            if (selectedBank == null) {
                Text("Select a bank")
            } else {
                BankCard(selectedBank)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            bankList.forEach { account ->
                Box(modifier = Modifier.clickable {
                    onSelectBank(account)
                    expanded = !expanded
                }) {
                    BankCard(bank = account)
                }
            }
        }
    }
}

@Composable
fun BankCard(bank: Bank) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Bank image
            Image(
                painter = painterResource(R.drawable.carrot_filled), // Don't use url
                contentDescription = "${bank.name} logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Bank info
            Column {
                Text(
                    text = bank.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = bank.balance.amount,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (bank.isExternal) "External" else "Internal",
                    style = MaterialTheme.typography.titleMedium
                )
                // You can add more details here later (type, balance, etc.)
            }
        }
    }
}