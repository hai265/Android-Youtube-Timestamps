package com.hai265.timestamper.ui.screens.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.hai265.timestamper.ComposeYouTubePlayer

//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-6261&t=xjloAEfEmnkGJuPR-0
//https://www.figma.com/design/9GKdOD5q3yAT0mKgrcGmpf/Android-Youtube-Timestamp-Tool?node-id=1-5026&t=xjloAEfEmnkGJuPR-0
@Composable
fun VideoScreen() {
    //TODO: Viewmodel, pass in id (not video id, timestamp id?, in viewmodel load from repo)
    Column {
        ComposeYouTubePlayer("tQDO-uVCl40")
    }
}