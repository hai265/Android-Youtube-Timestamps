package com.hai265.timestamper.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.ui.fakes.fakeTimestamp1
import com.hai265.timestamper.ui.screens.editor.formatDurationToHHMMSS
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

@Composable
fun BottomSheet() {
    val activity = LocalActivity.current as androidx.fragment.app.FragmentActivity

    LaunchedEffect(Unit) {
        val bottomSheet = ModalBottomSheet()

        bottomSheet.show(
            activity.supportFragmentManager,
            ModalBottomSheet.TAG
        )
    }
}

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        dialog?.window?.setSoftInputMode(
            //https://drive.google.com/file/d/143l-DGaEOnQXLEY1iTv2Rp9Cwkh08M1K/view?usp=sharing
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            //https://drive.google.com/file/d/1clBQJt_VuhglEFJFlWASKb6p7u31v_Gd/view?usp=sharing
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    TimestampEditorSheet(timestamp = fakeTimestamp1, onDismiss = {}, onSave = {})
                }
            }
        }
        return view
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

@Composable
fun TimestampEditorSheet(
    timestamp: Timestamp,
    onDismiss: () -> Unit,
    onSave: (Timestamp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val hideSheet = {
        scope.launch { }.invokeOnCompletion { }
    }
    val textFieldState = rememberTextFieldState(initialText = timestamp.description)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TimestampEditorSheetColumn(timestamp, textFieldState, focusRequester, onSave, hideSheet)

}

@Composable
private fun TimestampEditorSheetColumn(
    timestamp: Timestamp,
    textFieldState: TextFieldState,
    focusRequester: FocusRequester,
    onSave: (Timestamp) -> Unit,
    hideSheet: () -> DisposableHandle
) {
    var isMultiline by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .imePadding()
    ) {
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
                    imeAction = ImeAction.Done
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
            )
            FilledIconButton(
                onClick = {
                    onSave(timestamp.copy(description = textFieldState.text.toString()))
                    hideSheet()
                }, modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Icon(Icons.Filled.Check, "Save")
            }
        }
    }
}