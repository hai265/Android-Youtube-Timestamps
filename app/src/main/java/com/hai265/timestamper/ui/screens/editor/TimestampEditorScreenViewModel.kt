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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

data class TimestampEditorState(
    val video: Video? = null,
    val timestamps: List<Timestamp> = listOf(),
    val newlyAddedTimestampId: Long? = null
)

data class Preferences(
    val hideKeyboardOnScreenTap: Boolean = false,
    val pauseAndResumeVideoOnEdit: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class TimestampEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepo: VideoRepository,
    private val timestampRepo: TimestampRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val videoId = savedStateHandle.toRoute<Navigables.VideoScreen>().id

    private val currentTime = MutableStateFlow(Duration.ZERO)
    private val descriptionUpdates = MutableStateFlow<Pair<Timestamp, String>?>(null)
    private val newlyAddedId = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch {
            descriptionUpdates
                .filterNotNull()
                .debounce(500)
                .collect { (timestamp, description) ->
                    timestampRepo.addOrUpdateTimestamp(timestamp.copy(description = description))
                }

        }
        viewModelScope.launch {
            currentTime
                .drop(1) //Drop default Duration.ZERO value
                .sample(1000)
                .collect { lastWatchedTimestamp ->
                    state.value.video?.videoId?.let { videoId ->
                        videoRepo.updateVideoLastWatched(videoId, lastWatchedTimestamp)
                    }
                }

        }
    }

    val state = combine(
        timestampRepo.getTimestamps(videoId),
        flow { emit(videoRepo.getVideoById(videoId)) },
        newlyAddedId
    ) { timestamps, video, newlyAddedId ->
        TimestampEditorState(video, timestamps, newlyAddedId)
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

    fun addTimestamp() {
        viewModelScope.launch {
            state.value.video?.videoId?.let {
                val newTimestampId = timestampRepo.addEmptyTimestamp(
                    it, currentTime.value
                )
                newlyAddedId.value = newTimestampId
            }
        }
    }

    fun upsertTimestamp(timestamp: Timestamp) {
        viewModelScope.launch {
            newlyAddedId.value = timestampRepo.addOrUpdateTimestamp(timestamp)
        }
    }

    fun getEmptyTimestamp(): Timestamp {
        return Timestamp(
            id = 0,
            videoId = videoId,
            timeMs = currentTime.value.inWholeMilliseconds,
            description = "",
        )
    }

    fun deleteTimestamp(timestamp: Timestamp) {
        viewModelScope.launch {
            timestampRepo.deleteTimestamp(timestamp)
        }
    }

    fun updateCurrentTime(duration: Duration) {
        currentTime.value = duration
        Log.d("TimestampEditorViewModel", "current time:${duration.inWholeSeconds} ")
    }

    fun updateDescription(timestamp: Timestamp, description: String) {
        viewModelScope.launch {
            descriptionUpdates.emit(timestamp to description)
        }
    }
}
