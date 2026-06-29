package com.hai265.timestamper.screens.editor


import android_youtube_timestamps.sharedui.generated.resources.Res
import android_youtube_timestamps.sharedui.generated.resources.add
import android_youtube_timestamps.sharedui.generated.resources.close
import android_youtube_timestamps.sharedui.generated.resources.settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.window.core.layout.WindowSizeClass
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.fakeTimestamp1
import com.hai265.timestamper.screens.fakeTimestampList
import com.hai265.timestamper.screens.formatDurationToHHMMSS
import com.hai265.timestamper.screens.timestampeditor.TimestampEditorSheet
import com.hai265.timestamper.screens.youtubeplayer.ComposeYouTubePlayer
import com.hai265.timestamper.screens.youtubeplayer.YouTubePlayerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration
import kotlin.uuid.Uuid

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0

/*
TODO:
- Player Controls (Pause / Play), skip +/- 5 secs
- Add share timestamps as string (same as video list screen)
- Emtpy text default text (empty and light color)
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimestampViewerScreen(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass,
    insetsController: InsetsController,
    orientationController: OrientationController
) {
    val viewmodel: TimestampViewerViewModel = koinViewModel()
    val state by viewmodel.state.collectAsState()
    val preferences by viewmodel.preferences.collectAsState()
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    //TODO: Change to rememberSavable so timestmap editor appears on rotation
    var bottomSheetState by remember { mutableStateOf<BottomSheetState>(BottomSheetState.Hidden) }
    val scope = rememberCoroutineScope()

    val video = state.video
    val controller = koinInject<YouTubePlayerController>()
    var newlyAddedTimestampId by rememberSaveable { mutableStateOf<Uuid?>(null) }

    val videoPlayer = remember(video) {
        movableContentOf {
            video?.let {
                ComposeYouTubePlayer(
                    youtubeId = video.youtubeId,
                    onCurrentTime = viewmodel::updateCurrentTime,
                    controller = controller,
                    startingTime = video.lastPlayed,
                    onFullScreen = {
                        orientationController.landscape()
                    },
                    onExitFullScreen = {
                        orientationController.portrait()
                    },
                    onTapAddTimestamp = {
                        video.id.let {
                            bottomSheetState = BottomSheetState.EditTimestamp(
                                Timestamp(
                                    id = Uuid.random(),
                                    videoId = it,
                                    time = state.playerTime
                                )
                            )
                        }
                    },
                )
            }
        }
    }
    if (preferences.pauseAndResumeVideoOnEdit) {
        LaunchedEffect(bottomSheetState) {
            when (bottomSheetState) {
                BottomSheetState.Hidden -> controller.play()
                else -> controller.pause()
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                video?.id?.let {
                    bottomSheetState = BottomSheetState.EditTimestamp(
                        Timestamp(
                            id = Uuid.random(),
                            videoId = it,
                            time = state.playerTime
                        )
                    )
                }
            }) {
                Icon(painterResource(Res.drawable.add), "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        val timestampsList: @Composable (Boolean) -> Unit = { textSingleLine ->
            TimestampList(
                timestamps = state.timestamps,
                onDelete = viewmodel::deleteTimestamp,
                onTimestampClick = { duration -> controller.seekTo(duration) },
                onDescriptionClick = {
                    bottomSheetState = BottomSheetState.EditTimestamp(it)
                },
                highlightedId = newlyAddedTimestampId,
                textSingleLine = textSingleLine,
                onCLickSettings = { showSettingsDialog = true }
            )
        }
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            insetsController.hideSystemBars()
            val layoutDirection = LocalLayoutDirection.current
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .background(Color.Black)
                        .weight(0.7f)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier.width(
                            innerPadding.calculateLeftPadding(
                                layoutDirection
                            )
                        )
                    )
                    videoPlayer()
                }
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxSize()
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            end = innerPadding.calculateEndPadding(layoutDirection)
                        )
                ) { timestampsList(true) }
            }
        } else {
            insetsController.showSystemBars()
            Column(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxWidth()
                        .padding(top = innerPadding.calculateTopPadding())
                ) {
                    videoPlayer()
                }
                timestampsList(false)
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

    when (bottomSheetState) {
        is BottomSheetState.Hidden -> {} // Don't show sheet
        is BottomSheetState.EditTimestamp -> {
            val timestamp = (bottomSheetState as BottomSheetState.EditTimestamp).timestamp
            TimestampEditorSheet(
                onDismiss = { bottomSheetState = BottomSheetState.Hidden },
                onAddTimestamp = { addedTimestampId ->
                    scope.launch {
                        newlyAddedTimestampId = addedTimestampId
                        delay(1000)
                        newlyAddedTimestampId = null
                    }
                },
                timestamp = timestamp,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            insetsController.reset()
        }
    }
}


@Composable
fun TimestampList(
    timestamps: List<Timestamp>,
    onDelete: (timestamp: Timestamp) -> Unit,
    onTimestampClick: (Duration) -> Unit,
    onDescriptionClick: (Timestamp) -> Unit,
    highlightedId: Uuid?,
    textSingleLine: Boolean,
    onCLickSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(highlightedId) {
        if (highlightedId != null) {
            val index = timestamps.indexOfFirst { it.id == highlightedId }
            if (index != -1) {
                listState.animateScrollToItem(index + 1) // +1 for the settings header item
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
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
                        painter = painterResource(Res.drawable.settings),
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
                onTimestampDescriptionUpdate = { onDescriptionClick(timestamp) },
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
    onTimestampDescriptionUpdate: () -> Unit,
    newlyAdded: Boolean,
    textSingleLine: Boolean,
    modifier: Modifier = Modifier
) {
    val highlight = MaterialTheme.colorScheme.primaryContainer

    val backgroundColor by animateColorAsState(
        targetValue = if (newlyAdded) highlight else Color.Transparent,
        animationSpec = tween(durationMillis = if (newlyAdded) 0 else 500, easing = LinearEasing),
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
            text = timestamp.time.formatDurationToHHMMSS(),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier
                .clickable {
                    onTimestampClick(timestamp.time)
                }
                .padding(start = 16.dp)
                .widthIn(min = 72.dp)
        )

        Text(
            text = timestamp.description,
            maxLines = if (textSingleLine) 1 else 50,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onTimestampDescriptionUpdate)
        )
        Icon(
            painter = painterResource(Res.drawable.close),
            contentDescription = "Delete Timestamp",
            modifier = Modifier.clickable(onClick = onClickDelete)
        )
    }
}

@Composable
fun PreferencesDialog(
    onDismiss: () -> Unit,
    preferences: Preferences,
    viewModel: TimestampViewerViewModel
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

sealed interface BottomSheetState {
    object Hidden : BottomSheetState
    data class EditTimestamp(val timestamp: Timestamp) : BottomSheetState
}

@Preview
@Composable
fun TimestampListPreview() {
    TimestampList(
        timestamps = fakeTimestampList,
        onDelete = { },
        onTimestampClick = { },
        onDescriptionClick = { },
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
            id = Uuid.fromLongs(1, 1),
            videoId = Uuid.fromLongs(1, 1),
            time = Duration.ZERO,
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
        {}, {}
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
        {}, {}
    )
}