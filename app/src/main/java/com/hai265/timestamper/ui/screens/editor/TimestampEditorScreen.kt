package com.hai265.timestamper.ui.screens.editor

import android.view.ViewTreeObserver
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.ui.fakes.fakeTimestamp1
import com.hai265.timestamper.ui.fakes.fakeTimestampList
import com.hai265.timestamper.ui.screens.youtubeplayer.ComposeYouTubePlayer
import com.hai265.timestamper.ui.screens.youtubeplayer.YouTubePlayerController
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0

/*
TODO:
- phone horizontal mode (bug, config changes reloads video)
- Player Controls (Pause / Play), skip +/- 5 secs
- when add timestamp jump to timestamp and automatically open keyboard
- when press add timestamp add editor similar to Microsoft TO DO (probably better since can add stuff like tags, edit timestamp, color, etc)
- when press add timestamp pause video, exit keyboard resume playback (configurable)
- add separate settings / dialog for settings
- when open keyboard allow scroll down to view timestamps below (BUG currently: when timestamp added keyboard doesn't appear until scroll down)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimestampEditorScreen(windowSize: WindowWidthSizeClass) {
    val viewmodel = hiltViewModel<TimestampEditorViewModel>()
    val state by viewmodel.state.collectAsState()
    val preferences by viewmodel.preferences.collectAsState()
    LocalFocusManager.current
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var showTimestampSheet by rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val video = state.video
    val controller = remember { YouTubePlayerController() }
    val sheetState = rememberModalBottomSheetState()

    val videoPlayer = remember(video) {
        movableContentOf {
            if (video != null) {
                ComposeYouTubePlayer(
                    videoId = video.videoId,
                    onCurrentTime = viewmodel::updateCurrentTime,
                    controller = controller,
                    startingTime = video.lastPlayed,
                )
            }
        }
    }
    if (preferences.pauseAndResumeVideoOnEdit) {
        LaunchedEffect(showTimestampSheet) {
            when (showTimestampSheet) {
                true -> controller.pause()
                false -> controller.play()
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showTimestampSheet = true
            }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        if (video != null) {
            val timestampsList: @Composable (Boolean) -> Unit = { textSingleLine ->
                TimestampList(
                    timestamps = state.timestamps,
                    onDelete = viewmodel::deleteTimestamp,
                    onTimestampClick = { duration -> controller.seekTo(duration) },
                    updateDescription = viewmodel::updateDescription,
                    highlightedId = state.newlyAddedTimestampId,
                    textSingleLine = textSingleLine,
                    onCLickSettings = { showSettingsDialog = true }
                )
            }
            if (windowSize == WindowWidthSizeClass.Medium || windowSize == WindowWidthSizeClass.Expanded) {
                Row {
                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .windowInsetsPadding(WindowInsets(0))

                    ) { videoPlayer() }
                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                    ) { timestampsList(true) }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxSize()
                ) {
                    videoPlayer()
                    timestampsList(false)
                }
            }
        }
    }

    if (showSettingsDialog) {
        PreferencesDialog(
            onDismiss = { showSettingsDialog = false },
            preferences = preferences,
            viewModel = viewmodel
        )
    }

    if (showTimestampSheet) {
        val emptyTimestamp = viewmodel.getEmptyTimestamp()
        TimestampEditorSheet(
            onDismiss = { showTimestampSheet = false },
            onSave = { timestamp -> viewmodel.upsertTimestamp(timestamp) },
            sheetState = sheetState,
            timestamp = emptyTimestamp,
        )
    }
}


@Composable
fun TimestampList(
    timestamps: List<Timestamp>,
    onDelete: (timestamp: Timestamp) -> Unit,
    onTimestampClick: (Duration) -> Unit,
    updateDescription: (Timestamp, String) -> Unit,
    highlightedId: Long?,
    textSingleLine: Boolean,
    onCLickSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable(onClick = onCLickSettings),
                ) {
                    Text("Editor Settings")
                    Spacer(modifier.size(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "Preferences",
                    )
                }
            }
        }
        items(timestamps, key = { it.id }) { timestamp ->
            TimestampItem(
                timestamp,
                onClickDelete = { onDelete(timestamp) },
                onTimestampClick = onTimestampClick,
                onTimestampDescriptionUpdate = { newDescription ->
                    updateDescription(
                        timestamp,
                        newDescription
                    )
                },
                newlyAdded = timestamp.id == highlightedId,
                textSingleLine = textSingleLine
            )
        }
    }
}

@Composable
fun TimestampItem(
    timestamp: Timestamp,
    onClickDelete: () -> Unit,
    onTimestampClick: (Duration) -> Unit,
    onTimestampDescriptionUpdate: (String) -> Unit,
    newlyAdded: Boolean,
    textSingleLine: Boolean,
    modifier: Modifier = Modifier
) {
    val textFieldState = rememberTextFieldState(initialText = timestamp.description)

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text }
            .distinctUntilChanged()
            .collect { newText ->
                onTimestampDescriptionUpdate(newText.toString())
            }
    }
    val highlight = MaterialTheme.colorScheme.primaryContainer
    var targetColor by remember(newlyAdded) {
        mutableStateOf(if (newlyAdded) highlight else Color.Transparent)
    }

    LaunchedEffect(newlyAdded) {
        if (newlyAdded) {
            delay(1000)
            targetColor = Color.Transparent
        }
    }
    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "highlight"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatMilisToHHMMSS(timestamp.timeMs),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier
                .clickable {
                    onTimestampClick(timestamp.timeMs.toDuration(DurationUnit.MILLISECONDS))
                }
                .padding(start = 16.dp)
                .widthIn(min = 72.dp)
        )

        TextField(
            state = textFieldState, colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            ),
            placeholder = { Text("Description") },
            lineLimits = if (textSingleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
            modifier = Modifier
                .weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.close),
            contentDescription = "Delete Timestamp",
            modifier = Modifier.clickable(onClick = onClickDelete)
        )
    }
}

@Composable
fun PreferencesDialog(
    onDismiss: () -> Unit,
    preferences: Preferences,
    viewModel: TimestampEditorViewModel
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Close keyboard tap screen")
                    Switch(
                        checked = preferences.hideKeyboardOnScreenTap,
                        onCheckedChange = {
                            viewModel.updateHideKeyboardOnScreenTap(it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pause video on edit")
                    Switch(
                        checked = preferences.pauseAndResumeVideoOnEdit,
                        onCheckedChange = {
                            viewModel.updatePauseAndResumeOnEdit(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimestampEditorSheet(
    timestamp: Timestamp,
    onDismiss: () -> Unit,
    onSave: (Timestamp) -> Unit,
    sheetState: SheetState,
) {
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {},
    ) {
        TimestampEditorSheetColumn(timestamp, textFieldState, focusRequester, onSave, hideSheet)
    }
}

@Composable
private fun TimestampEditorSheetColumn(
    timestamp: Timestamp,
    textFieldState: TextFieldState,
    focusRequester: FocusRequester,
    onSave: (Timestamp) -> Unit,
    hideSheet: () -> DisposableHandle
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Timestamp: ${formatMilisToHHMMSS(timestamp.timeMs)}",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        Row {
            TextField(
                state = textFieldState,
                placeholder = { Text("Description") },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
            )
            FilledIconButton(onClick = {
                onSave(timestamp.copy(description = textFieldState.text.toString()))
                hideSheet()
            }, modifier = Modifier.padding(start = 16.dp)) {
                Icon(Icons.Filled.Check, "Save")
            }
        }
    }
}

fun formatMilisToHHMMSS(millis: Long): String {
    val duration = millis.milliseconds
    return duration.toComponents { hours, minutes, seconds, _ ->
        if (hours >= 1L) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}

@Preview
@Composable
fun TimestampListPreview() {
    TimestampList(
        timestamps = fakeTimestampList,
        onDelete = { },
        onTimestampClick = { },
        updateDescription = { _, _ -> },
        highlightedId = null,
        textSingleLine = false,
        onCLickSettings = {},
    )
}

@Preview
@Composable
fun TimestampItemPreview() {
    TimestampItem(
        timestamp = Timestamp(
            id = 0,
            videoId = "",
            timeMs = 0L,
            description = "timestamp description"
        ),
        onClickDelete = {},
        onTimestampClick = {},
        onTimestampDescriptionUpdate = {},
        newlyAdded = true,
        textSingleLine = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TimestampSheetPortraitPreview() {
    TimestampEditorSheet(
        fakeTimestamp1,
        {},
        onSave = {},
        sheetState = rememberStandardBottomSheetState(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun TimestampSheetLandscapePreview() {
    TimestampEditorSheet(
        fakeTimestamp1,
        {},
        onSave = {},
        sheetState = rememberStandardBottomSheetState(),
    )
}