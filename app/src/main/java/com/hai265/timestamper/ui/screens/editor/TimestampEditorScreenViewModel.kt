package com.hai265.timestamper.ui.screens.editor

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.PreferencesRepository
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.ui.Navigables
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration

data class TimestampEditorState(
    val video: Video? = null,
    val timestamps: List<Timestamp> = listOf(),
    val playerTime: Duration = Duration.ZERO,
)

data class Preferences(
    val hideKeyboardOnScreenTap: Boolean = false,
    val pauseAndResumeVideoOnEdit: Boolean = false,
)

@OptIn(FlowPreview::class)
class TimestampViewerViewModel(
    savedStateHandle: SavedStateHandle,
    private val videoRepo: VideoRepository,
    private val timestampRepo: TimestampRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val youtubeId = savedStateHandle.toRoute<Navigables.VideoScreen>().id

    private val _currentTime = MutableStateFlow(Duration.ZERO)

    init {
        viewModelScope.launch {
            _currentTime
                .drop(1) //Drop default Duration.ZERO value
                .sample(1000)
                .collect { lastWatchedTimestamp ->
                    state.value.video?.id?.let { videoId ->
                        videoRepo.updateVideoLastWatched(videoId, lastWatchedTimestamp)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        flow { emit(videoRepo.getVideoByYoutubeId(youtubeId)) }
            .flatMapLatest { video ->
                if (video != null) {
                    combine(
                        timestampRepo.getTimestamps(video.id),
                        _currentTime
                    ) { timestamps, currentTime ->
                        TimestampEditorState(video, timestamps, currentTime)
                    }
                } else {
                    flowOf(TimestampEditorState())
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = TimestampEditorState()
            )

    val preferences = combine(
        preferencesRepository.hideKeyboardOnScreenTap(),
        preferencesRepository.pauseVideoOnKeyboardVisible()
    )
    { hideKeyboardOnScreenTap, pauseAndResumeVideoOnEdit ->
        Preferences(
            hideKeyboardOnScreenTap = hideKeyboardOnScreenTap,
            pauseAndResumeVideoOnEdit = pauseAndResumeVideoOnEdit,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Preferences()
        )

    fun updateHideKeyboardOnScreenTap(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHideKeyboardOnScreenTap(enabled)
        }
    }

    fun updatePauseAndResumeOnEdit(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updatePauseVideoOnKeyboardVisible(enabled)
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
