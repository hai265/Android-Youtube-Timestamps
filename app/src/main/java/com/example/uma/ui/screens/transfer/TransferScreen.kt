package com.example.uma.ui.screens.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TransferScreen(modifier: Modifier = Modifier) {
    val viewModel: TransferViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Content(viewModel.textFieldState,{viewModel.onTransfer()},state.canTransfer)
}

@Composable
private fun Content(textFieldState: TextFieldState, onTransfer: () -> Unit, canTransfer: Boolean, modifier: Modifier = Modifier.fillMaxSize()) {
    Column {
        TextField(state = textFieldState)
        Text("Dropdown1")
        Text("Dropdown2")
        Button(onClick = {onTransfer()}, enabled = canTransfer) {
            Text("Transfer")
        }
    }
}



@Preview
@Composable
fun TransferScreenPreview() {
    Content(TextFieldState("0.00"), {},false)
}