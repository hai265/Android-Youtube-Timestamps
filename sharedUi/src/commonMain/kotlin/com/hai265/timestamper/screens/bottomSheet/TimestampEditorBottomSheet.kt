package com.hai265.timestamper.screens.bottomSheet

import androidx.compose.runtime.Composable

@Composable
expect fun BottomSheet(onDismiss: () -> Unit, content: @Composable (hideSheet: () -> Unit) -> Unit)