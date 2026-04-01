package com.hai265.timestamper.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.ui.Navigables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimestampEditorState(
    val video: Video? = null,
    val timestamps: List<Timestamp> = listOf()
)

@HiltViewModel
class TimestampEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepo: VideoRepository,
    private val timestampRepo: TimestampRepository
) : ViewModel() {
    private val videoId = savedStateHandle.toRoute<Navigables.VideoScreen>().id

    val state = timestampRepo.getTimestamps(videoId)
        .combine(flow { emit(videoRepo.getVideoById(videoId)) }) { timestamps, video ->
            TimestampEditorState(video, timestamps)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TimestampEditorState()
        )

    fun addTimestamp(secondsMs: Long) {
        viewModelScope.launch {
            state.value.video?.videoId?.let {
                timestampRepo.addEmptyTimestamp(it, secondsMs)
            }
        }
    }

    fun deleteTimestamp(timestamp: Timestamp) {
        viewModelScope.launch {
            timestampRepo.deleteTimestamp(timestamp)
        }
    }
}