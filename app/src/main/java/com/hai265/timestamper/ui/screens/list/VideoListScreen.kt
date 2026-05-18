package com.hai265.timestamper.ui.screens.list

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-5026&t=xjloAEfEmnkGJuPR-0
//TODO: Duplicate file number append to extention e.g name.yaml(1) instead of name(1).yaml
@OptIn(ExperimentalMaterial3Api::class)
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
    val coroutineScope = rememberCoroutineScope()
    val gridNumCells = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            1
        }

        WindowWidthSizeClass.Medium -> {
            2
        }

        WindowWidthSizeClass.Expanded -> {
            3 //TODO: Fix grid size uneven when 3
        }

        else -> {
            1
        }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewmodel.exportVideo(uri)
            Toast.makeText(
                context,
                "Timestamps Successfully Exported",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewmodel.importTimestamps(uri)
            //TODO: SUbscribe to mutable shared flow to launch toast
            Toast.makeText(
                context,
                "Timestamps Successfully Imported",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Text("Videos")
                },
                actions = {
                    MenuDropDown(onTapExportVideo = {
                        exportLauncher.launch("timestamps-${Clock.System.now()}")
                    }, onTapImportVideo = {
                        importLauncher.launch(arrayOf("*/*"))
                    })
                },
                scrollBehavior = scrollBehavior
            )
        },
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
            onExportVideo = { video, uri ->
                viewmodel.exportVideo(
                    uri,
                )
            },
            onTapShareVideo = { video ->
                coroutineScope.launch {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, viewmodel.getTimestampsAsString(video.id))
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
            },
            listState = listState,
            gridNumCells = gridNumCells,
            paddingValues = innerPadding,
        )
    }

    if (addVideoDialog) {
        AddVideoDialog(
            onDismissRequest = { addVideoDialog = false },
            onConfirmation = { url ->
                coroutineScope.launch {
                    val videoResult = viewmodel.addVideo(url)
                    handleVideoResult(context, videoResult, { addVideoDialog = false })
                }
            },
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
    onExportVideo: (video: Video, uri: Uri) -> Unit,
    onTapShareVideo: (video: Video) -> Unit,
    listState: LazyGridState,
    gridNumCells: Int,
    paddingValues: PaddingValues,
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
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 88.dp,
            )
        ) {
            items(videoList) { video ->
                VideoItem(
                    video = video,
                    onTap = { onTapVideo(video.id) },
                    onTapDeleteVideo = { onDeleteVideo(video) },
                    onTapExportVideo = { uri -> onExportVideo(video, uri) },
                    onTapShareVideo = { onTapShareVideo(video) },
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
    onTapDeleteVideo: () -> Unit,
    onTapExportVideo: (Uri) -> Unit,
    onTapShareVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            onTapExportVideo(uri)
            Toast.makeText(
                context,
                "Timestamps Successfully Exported",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Column(modifier = modifier.clickable(onClick = onTap)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = null,
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
        ) {
            val timezoneId = android.icu.util.TimeZone.getDefault().id
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    video.videoTitle ?: "Video ID: ${video.id}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    "Last Edited: ${video.lastEdited.toFormattedString(timezoneId)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            VideoDropdownMenu(
                onTapDeleteVideo = onTapDeleteVideo,
                onShareTimestamps = onTapShareVideo,
            )
        }
    }
}

private fun Instant.toFormattedString(
    timezoneId: String?,
): String {
    val timezone = timezoneId ?: TimeZone.UTC.id
    val localDateTime = this.toLocalDateTime(TimeZone.of(timezone))
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.of(timezone))
    val format = if (localDateTime.day != currentTime.day) {
        DATE_FORMAT
    } else {
        TIME_FORMAT
    }
    return format.format(localDateTime)
}

private val DATE_FORMAT = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
    char(',')
    char(' ')
    year()
}

private val TIME_FORMAT = LocalDateTime.Format {
    amPmHour(Padding.ZERO)
    char(':')
    minute()
    char(' ')
    amPmMarker("AM", "PM")
}

@Composable
fun VideoDropdownMenu(
    onTapDeleteVideo: () -> Unit,
    onShareTimestamps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    expanded = false
                    onTapDeleteVideo()
                }
            )
            DropdownMenuItem(
                text = { Text("Share Timestamps") },
                onClick = {
                    expanded = false
                    onShareTimestamps()
                }
            )
        }
    }
}

@Composable
fun MenuDropDown(
    onTapExportVideo: () -> Unit,
    onTapImportVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Import") },
                onClick = {
                    expanded = false
                    onTapImportVideo()
                }
            )
            DropdownMenuItem(
                text = { Text("Export") },
                onClick = {
                    expanded = false
                    onTapExportVideo()
                }
            )
        }
    }
}

@Composable
fun ExportDropdownMenu(
    onTapExportVideo: () -> Unit,
    onTapShareVideo: () -> Unit,
    expanded: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text("Export Backup") },
                onClick = onTapExportVideo
            )
            DropdownMenuItem(
                text = { Text("Share Timestamps") },
                onClick = onTapShareVideo
            )
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
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(video.videoTitle ?: video.id)
                    }
                    append("will be removed from your list.")
                }
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
        onExportVideo = { _, _ -> },
        paddingValues = PaddingValues.Zero,
        onTapShareVideo = {}
    )
}

@Preview
@Composable
private fun VideoItemPreview() {
    VideoItem(
        video = fakeVideo1,
        onTap = {},
        onTapDeleteVideo = {},
        onTapExportVideo = {},
        onTapShareVideo = {})
}


