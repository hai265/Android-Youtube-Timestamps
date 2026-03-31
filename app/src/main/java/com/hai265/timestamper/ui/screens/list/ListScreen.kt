package com.hai265.timestamper.ui.screens.list

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.hai265.timestamper.R
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.ui.fakes.fakeVideo1
import com.hai265.timestamper.ui.fakes.fakeVideoList
import kotlinx.coroutines.launch

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-5026&t=xjloAEfEmnkGJuPR-0
@Composable
fun ListScreen(onTapVideo: (id: String) -> Unit) {
    val viewmodel: ListScreenViewModel = hiltViewModel()
    val state by viewmodel.state.collectAsState()
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        VideoList(state.videos, onTapVideo)
        Button(onClick = { openDialog = true }) {
            Text("Add video")
        }
    }

    if (openDialog) {
        val composableScope = rememberCoroutineScope()
        AlertDialogExample(
            onDismissRequest = { openDialog = false },
            onConfirmation = { url ->
                composableScope.launch {
                    val videoResult = viewmodel.addVideo(url)
                    when (videoResult) {
                        VideoResult.Success -> {
                            openDialog = false
                        }
                        //TODO:  VideoResult errors
                        is VideoResult.InvalidUrl -> {
                            Toast.makeText(
                                context,
                                "Invalid Url: ${videoResult.url}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun VideoList(
    videoList: List<Video>,
    onTapVideo: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(videoList) { video ->
            VideoThumbnail(
                video = video,
                onTap = { onTapVideo(video.videoId) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VideoThumbnail(video: Video, onTap: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clickable(onClick = onTap)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = null,
            //TODO placeholder: grey thumbnail like youtube
            placeholder = painterResource(R.drawable.test_thumbnail1),
            error = painterResource(R.drawable.test_thumbnail1),
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(video.videoTitle ?: "Video ID: ${video.videoId}")
        Text("Updated today") //Read from video.timestamp
    }
}

@Composable
fun AlertDialogExample(
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
                Text("Dismiss")
            }
        }
    )
}


@Preview
@Composable
private fun VideoListPreview() {
    VideoList(
        videoList = fakeVideoList,
        onTapVideo = {},
    )
}

@Preview
@Composable
private fun VideoThumbnailPreview() {
    VideoThumbnail(video = fakeVideo1, onTap = {})
}


