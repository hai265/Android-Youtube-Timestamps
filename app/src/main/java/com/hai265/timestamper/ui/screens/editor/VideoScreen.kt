package com.hai265.timestamper.ui.screens.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hai265.timestamper.ui.ComposeYouTubePlayer

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0
//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-5026&t=xjloAEfEmnkGJuPR-0
@Composable
fun VideoScreen() {
    val viewmodel = hiltViewModel<VideoScreenViewModel>()
    val state by viewmodel.state.collectAsState()

    val videoId = state.video?.videoId
    if (videoId != null) {
        Column {
            ComposeYouTubePlayer(videoId)
//            Timestamps()
        }
    }
}