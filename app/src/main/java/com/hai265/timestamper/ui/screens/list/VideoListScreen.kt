package com.hai265.timestamper.ui.screens.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
//TODO: Floating action button
@Composable
fun VideoListScreen(onTapVideo: (id: String) -> Unit, windowSize: WindowWidthSizeClass) {
    val viewmodel: VideoListScreenViewModel = hiltViewModel()
    val state by viewmodel.state.collectAsState()
    var addVideoDialog by rememberSaveable { mutableStateOf(false) }
    var videoToDeleteDialog by remember { mutableStateOf<Video?>(null) }

    val context = LocalContext.current
    val listState = rememberLazyGridState()
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }
    val gridNumCells = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            1
        }

        WindowWidthSizeClass.Medium -> {
            2
        }

        WindowWidthSizeClass.Expanded -> {
            3
        }

        else -> {
            1
        }
    }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { addVideoDialog = true },
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
            onDeleteVideo = { video -> videoToDeleteDialog = video },
            listState = listState,
            gridNumCells = gridNumCells,
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding(),
            )
        )
    }

    if (addVideoDialog) {
        val composableScope = rememberCoroutineScope()
        AddVideoDialog(
            onDismissRequest = { addVideoDialog = false },
            onConfirmation = { url ->
                composableScope.launch {
                    val videoResult = viewmodel.addVideo(url)
                    handleVideoResult(context, videoResult, { addVideoDialog = false })
                }
            }
        )
    }

    videoToDeleteDialog?.let { video ->
        DeleteConfirmationDialog(
            video = video,
            onDismissRequest = { videoToDeleteDialog = null },
            onConfirmation = {
                viewmodel.deleteVideo(video)
                Toast.makeText(
                    context,
                    "Video Successfully Deleted",
                    Toast.LENGTH_LONG
                ).show()
                videoToDeleteDialog = null
            }
        )
    }
}

@Composable
private fun VideoListScreen(
    videoList: List<Video>,
    onTapVideo: (id: String) -> Unit,
    onDeleteVideo: (video: Video) -> Unit,
    listState: LazyGridState,
    gridNumCells: Int,
    modifier: Modifier = Modifier
) {
    if (videoList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Add videos using the \"Add Video\" button",
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyVerticalGrid(
            state = listState,
            modifier = modifier,
            columns = GridCells.Fixed(count = gridNumCells),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
}

@Composable
private fun VideoItem(
    video: Video,
    onTap: () -> Unit,
    onTapDeleteVideo: (Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onTap)) {
        Box {
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
            IconButton(
                onClick = { onTapDeleteVideo(video) }, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Video",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(video.videoTitle ?: "Video ID: ${video.videoId}", style = MaterialTheme.typography.titleMedium)
            }
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


@Composable
fun DeleteConfirmationDialog(
    video: Video,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = { Text("Delete video?") },
        text = {
            Text(
                "${video.videoTitle ?: video.videoId} will be removed from your list."
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
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
        listState = LazyGridState(),
        gridNumCells = 1,
    )
}

@Preview
@Composable
private fun VideoItemPreview() {
    VideoItem(video = fakeVideo1, onTap = {}, onTapDeleteVideo = {})
}


