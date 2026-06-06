package com.hai265.timestamper.ui.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TestComposable() {
    val viewmodel: TestViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    Column {
        Spacer(Modifier.size(64.dp))
        Button(onClick = {
            scope.launch {
                text = viewmodel.export()
            }
        }) {
            Text("Export")
        }
        Text(text)
    }


}

