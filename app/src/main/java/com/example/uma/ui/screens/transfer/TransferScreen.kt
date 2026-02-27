package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.uma.data.models.Bank

@Composable
fun TransferScreen(modifier: Modifier = Modifier) {
    val viewModel: TransferViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    viewModel.fetchAccounts()

    Content(
        viewModel.textFieldState, { viewModel.onTransfer() },
        sourceAccounts = state.list,
        targetAccounts = state.list,
        canTransfer = state.canTransfer,
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
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(modifier = modifier) {
        TextField(state = textFieldState)
        LongBasicDropdownMenu(sourceAccounts)
        LongBasicDropdownMenu(targetAccounts)
        Button(onClick = { onTransfer() }, enabled = canTransfer) {
            Text("Transfer")
        }
    }
}


@Composable
fun LongBasicDropdownMenu(accounts: List<Bank>) {
    var expanded by remember { mutableStateOf(false) }
    // Placeholder list of 100 strings for demonstration

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Composable
fun BankItem(onClick: () -> Unit) {
    Row() {

    }

}


@Preview
@Composable
fun TransferScreenPreview() {
    Content(
        TextFieldState("0.00"),
        {},
        sourceAccounts = listOf(),
        targetAccounts = listOf(),
        canTransfer = false
    )
}