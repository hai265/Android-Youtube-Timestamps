package com.hai265.timestamper.ui.screens.test

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.hai265.timestamper.data.platform
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TestComposable() {
    val viewmodel: TestViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

    Text(platform())


}

