package com.hai265.timestamper.ui.screens.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.ui.fakes.fakeTimestampList
import com.hai265.timestamper.ui.screens.youtubeplayer.ComposeYouTubePlayer
import com.hai265.timestamper.ui.screens.youtubeplayer.YouTubePlayerController
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0

/*
TODO:
- remember time left off
- Sort by time
- Player Controls (Pause / Play), skip +/- 5 secs
 */
@Composable
fun TimestampEditorScreen() {
    val viewmodel = hiltViewModel<TimestampEditorViewModel>()
    val state by viewmodel.state.collectAsState()

    val videoId = state.video?.videoId
    val controller = remember { YouTubePlayerController() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewmodel.addTimestamp() }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        if (videoId != null) {
            Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                ComposeYouTubePlayer(
                    videoId = videoId,
                    onCurrentTime = viewmodel::updateCurrentTime,
                    controller = controller
                )
                TimestampList(
                    timestamps = state.timestamps,
                    onDelete = viewmodel::deleteTimestamp,
                    onTimestampClick = { duration -> controller.seekTo(duration) },
                    updateDescription = viewmodel::updateDescription
                )
            }
        }
    }


}


@Composable
fun TimestampList(
    timestamps: List<Timestamp>,
    onDelete: (timestamp: Timestamp) -> Unit,
    onTimestampClick: (Duration) -> Unit,
    updateDescription: (Timestamp, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
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
                }
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
    Row(
        modifier = modifier.fillMaxWidth(),
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
            placeholder = { Text("Enter Description") },
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.close),
            contentDescription = "Delete Timestamp",
            modifier = Modifier.clickable(onClick = onClickDelete)
        )
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
    )
}