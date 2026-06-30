package com.hai265.timestamper.screens.timestampeditor

import android_youtube_timestamps.sharedui.generated.resources.Res
import android_youtube_timestamps.sharedui.generated.resources.check
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.screens.fakeTimestamp1
import com.hai265.timestamper.screens.formatDurationToHHMMSS
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimestampEditorSheet(
    timestamp: Timestamp,
    onDismiss: () -> Unit,
    onAddTimestamp: (Uuid) -> Unit,
) {
    val viewmodel: TimestampEditorViewModel = koinViewModel()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val hideSheet = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }
    val textFieldState = rememberTextFieldState(initialText = timestamp.description)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {},
    ) {
        TimestampEditorSheetContent(
            timestamp,
            textFieldState,
            focusRequester,
            { timestamp ->
                scope.launch {
                    val savedId = viewmodel.upsertTimestamp(timestamp)
                    onAddTimestamp(savedId)
                }
            },
            hideSheet
        )
    }
}

@Composable
private fun TimestampEditorSheetContent(
    timestamp: Timestamp,
    textFieldState: TextFieldState,
    focusRequester: FocusRequester,
    onSave: (Timestamp) -> Unit,
    hideSheet: () -> DisposableHandle
) {
    var isMultiline by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Timestamp: ${timestamp.time.formatDurationToHHMMSS()}",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        Row(verticalAlignment = if (isMultiline) Alignment.Bottom else Alignment.CenterVertically) {
            TextField(
                state = textFieldState,
                placeholder = { Text("Description") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onKeyboardAction = KeyboardActionHandler {
                    onSave(timestamp.copy(description = textFieldState.text.toString()))
                    hideSheet()
                },
                onTextLayout = { result ->
                    isMultiline = (result.invoke()?.lineCount ?: 0) > 1
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onVisibilityChanged { visible ->
                        if (visible) {
                            focusRequester.requestFocus()
                        }
                    }
            )
            FilledIconButton(
                onClick = {
                    onSave(timestamp.copy(description = textFieldState.text.toString()))
                    hideSheet()
                }, modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Icon(painterResource(Res.drawable.check), "Save")
            }
        }
    }
}

@Preview
@Composable
fun TestTimestampEditorSheetContentPreview() {
    TimestampEditorSheetContent(
        timestamp = fakeTimestamp1,
        textFieldState = TextFieldState(),
        focusRequester = FocusRequester(),
        onSave = {},
        hideSheet = {
            DisposableHandle { }
        }
    )
}