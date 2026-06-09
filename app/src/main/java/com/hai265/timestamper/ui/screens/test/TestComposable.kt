package com.hai265.timestamper.ui.screens.test

//import com.hai265.timestamper.data.kmp.platform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TestComposable() {
    val viewmodel: TestViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

//    Text(platform())


}

