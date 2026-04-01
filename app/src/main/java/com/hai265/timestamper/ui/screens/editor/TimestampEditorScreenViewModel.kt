package com.hai265.timestamper.ui.screens.editor

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class TimestampEditorState(
    val video: Video? = null,
    val timestamps: List<Timestamp> = listOf(),
    val currentTime: Duration = Duration.ZERO
)

@HiltViewModel
class TimestampEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepo: VideoRepository,
    private val timestampRepo: TimestampRepository
) : ViewModel() {
    private val videoId = savedStateHandle.toRoute<Navigables.VideoScreen>().id

    private val _currentTime = MutableStateFlow(Duration.ZERO)

    private var seekTo: ((Float) -> Unit)? = null

    val state = combine(
        timestampRepo.getTimestamps(videoId),
        flow { emit(videoRepo.getVideoById(videoId)) },
        _currentTime
    ) { timestamps, video, currentTime ->
        TimestampEditorState(video, timestamps, currentTime)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TimestampEditorState()
    )

    fun onPlayerReady(seekTo: (Float) -> Unit) {
        this.seekTo = seekTo
    }

    fun seekToTimestamp(duration: Duration) {
        seekTo?.invoke(duration.toDouble(DurationUnit.SECONDS).toFloat())
    }

    fun addTimestamp() {
        viewModelScope.launch {
            state.value.video?.videoId?.let {
                timestampRepo.addEmptyTimestamp(it, state.value.currentTime)
            }
        }
    }

    fun deleteTimestamp(timestamp: Timestamp) {
        viewModelScope.launch {
            timestampRepo.deleteTimestamp(timestamp)
        }
    }

    fun updateCurrentTime(duration: Duration) {
        _currentTime.value = duration
        Log.d("TimestampEditorViewModel", "current time:${duration.inWholeSeconds} ")
    }
}
