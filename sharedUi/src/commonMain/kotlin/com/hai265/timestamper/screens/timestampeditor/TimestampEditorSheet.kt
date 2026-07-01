package com.hai265.timestamper.screens.timestampeditor

import android_youtube_timestamps.sharedui.generated.resources.Res
import android_youtube_timestamps.sharedui.generated.resources.add
import android_youtube_timestamps.sharedui.generated.resources.check
import android_youtube_timestamps.sharedui.generated.resources.remove
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.hai265.timestamper.screens.durationSaver
import com.hai265.timestamper.screens.formatDurationToHHMMSS
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
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
    var currentTime by rememberSaveable(stateSaver = durationSaver) { mutableStateOf(timestamp.time) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {},
    ) {
        TimestampEditorSheetContent(
            textFieldState,
            currentTime,
            timestamp.time,
            focusRequester,
            { description, time ->
                scope.launch {
                    val savedId = viewmodel.upsertTimestamp(
                        timestamp.copy(
                            description = description,
                            time = time
                        )
                    )
                    onAddTimestamp(savedId)
                }
            },
            hideSheet,
            {
                currentTime += 1.seconds
            },
            {//Don't subtract time if it'll lead to negative time
                currentTime = maxOf(currentTime - 1.seconds, Duration.ZERO)
            }
        )
    }
}

@Composable
private fun TimestampEditorSheetContent(
    textFieldState: TextFieldState,
    currentTime: Duration,
    originalTime: Duration,
    focusRequester: FocusRequester,
    onSave: (description: String, time: Duration) -> Unit,
    hideSheet: () -> DisposableHandle,
    onTapAdd: () -> Unit,
    onTapMinus: () -> Unit
) {
    var isMultiline by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "Timestamp: ${currentTime.formatDurationToHHMMSS()}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(4.dp))
                TimeOffsetIndicator(currentTime, originalTime)
                IconButton(onClick = onTapMinus) {
                    Icon(painterResource(Res.drawable.remove), "Subtract Second")
                }
                IconButton(onClick = onTapAdd) {
                    Icon(painterResource(Res.drawable.add), "Add Second")
                }
            }
        }

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
                    onSave(textFieldState.text.toString(), currentTime)
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
                    onSave(textFieldState.text.toString(), currentTime)
                    hideSheet()
                }, modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Icon(painterResource(Res.drawable.check), "Save")
            }
        }
    }
}

@Composable
fun TimeOffsetIndicator(currentTime: Duration, originalTime: Duration) {
    val offset = (currentTime - originalTime)
    if (offset != Duration.ZERO) {
        Text(
            text = if (offset.isNegative()) "${offset.inWholeSeconds}" else "+${offset.inWholeSeconds}",
            color = if (offset.isNegative()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
fun TestTimestampEditorSheetContentPreview() {
    TimestampEditorSheetContent(
        textFieldState = TextFieldState(),
        focusRequester = FocusRequester(),
        currentTime = Duration.ZERO,
        originalTime = Duration.ZERO,
        onSave = { _, _ -> },
        hideSheet = {
            DisposableHandle { }
        },
        onTapAdd = {},
        onTapMinus = {},
    )
}