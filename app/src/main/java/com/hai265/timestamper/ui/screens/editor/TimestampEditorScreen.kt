package com.hai265.timestamper.ui.screens.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.ui.ComposeYouTubePlayer
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0

/*
TODO:
1. When add timestamp add from current time
2. Sort by time
3. When tap timestamp move player to that time
 */
@Composable
fun TimestampEditorScreen() {
    val viewmodel = hiltViewModel<TimestampEditorViewModel>()
    val state by viewmodel.state.collectAsState()

    val videoId = state.video?.videoId
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewmodel.addTimestamp(0L) }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        if (videoId != null) {
            Column(modifier = Modifier.padding(innerPadding)) {
                ComposeYouTubePlayer(videoId)
                TimestampList(timestamps = state.timestamps, onDelete = viewmodel::deleteTimestamp)
            }
        }
    }


}


@Composable
fun TimestampList(
    timestamps: List<Timestamp>,
    onDelete: (timestamp: Timestamp) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(timestamps) { timestamp ->
            TimestampItem(timestamp, onClickDelete = { onDelete(timestamp) })
        }
    }
}

@Composable
fun TimestampItem(timestamp: Timestamp, onClickDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(formatMilisToHHMMSS(timestamp.timeMs))
        Text("Description")
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
        val format = if (hours >= 1L) "%02d:%02d:%02d" else "%02d:%02d"
        String.format(Locale.US, format, hours, minutes, seconds)
    }
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
        onClickDelete = {}
    )
}