package com.hai265.timestamper.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.ui.fakes.fakeVideo1
import com.hai265.timestamper.ui.fakes.fakeVideoList
import com.hai265.timestamper.ui.handleVideoResult
import kotlinx.coroutines.launch

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-5026&t=xjloAEfEmnkGJuPR-0
@Composable
fun VideoListScreen(onTapVideo: (id: String) -> Unit) {
    val viewmodel: VideoListScreenViewModel = hiltViewModel()
    val state by viewmodel.state.collectAsState()
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { openDialog = true },
                icon = { Icon(Icons.Filled.Add, "Add Video") },
                text = { Text("Add Video") },
                expanded = showButton
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        VideoListScreen(
            videoList = state.videos,
            onTapVideo = onTapVideo,
            onDeleteVideo = viewmodel::deleteVideo,
            listState = listState,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }

    if (openDialog) {
        val composableScope = rememberCoroutineScope()
        AddVideoDialog(
            onDismissRequest = { openDialog = false },
            onConfirmation = { url ->
                composableScope.launch {
                    val videoResult = viewmodel.addVideo(url)
                    handleVideoResult(context, videoResult, { openDialog = false })
                }
            }
        )
    }
}

@Composable
private fun VideoListScreen(
    videoList: List<Video>,
    onTapVideo: (id: String) -> Unit,
    onDeleteVideo: (video: Video) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = 80.dp
        )
    ) {
        items(videoList) { video ->
            VideoItem(
                video = video,
                onTap = { onTapVideo(video.videoId) },
                onTapDeleteVideo = onDeleteVideo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VideoItem(
    video: Video,
    onTap: () -> Unit,
    onTapDeleteVideo: (Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onTap)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = null,
            //TODO placeholder: grey thumbnail like youtube
            placeholder = painterResource(R.drawable.loading_thumbnail),
            error = painterResource(R.drawable.default_thumbnail),
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(16f / 9f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(video.videoTitle ?: "Video ID: ${video.videoId}")
                Text("Updated today") //Read from video.timestamp
            }
            Button(onClick = { onTapDeleteVideo(video) }) { Text("Delete") }
        }
    }
}

@Composable
fun AddVideoDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (url: String) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    AlertDialog(
        title = {
            Text(text = "Add Video")
        },
        text = {
            TextField(
                state = textFieldState,
                label = { Text("Youtube URL") }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(textFieldState.text.toString())
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}


@Preview
@Composable
private fun VideoListPreview() {
    VideoListScreen(
        videoList = fakeVideoList,
        onTapVideo = {},
        onDeleteVideo = {},
        listState = LazyListState()
    )
}

@Preview
@Composable
private fun VideoItemPreview() {
    VideoItem(video = fakeVideo1, onTap = {}, onTapDeleteVideo = {})
}


