package com.hai265.timestamper.ui.screens.timestampeditor

import androidx.lifecycle.ViewModel
import com.hai265.timestamper.data.getYoutubeTimestampFromUrl
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration


sealed interface State {
    data object Initial : State
    data class AddTimestamp(
        val videoId: String,
        val time: Duration
    ) : State

    data object Finished : State //TODO: Message in finished?
}


@HiltViewModel
class TimestampDialogActivityViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Initial)
    val state = _state.asStateFlow()

    suspend fun addVideo(url: String): VideoResult {
        val videoResult = repo.addVideo(url)
        when (videoResult) {
            is VideoResult.InvalidUrl -> {
                _state.update { State.Finished }
            }

            is VideoResult.NetworkError -> {
                _state.update { State.Finished }
            }

            is VideoResult.Success -> {
                val id = videoResult.videoId
                val timestamp = getYoutubeTimestampFromUrl(url)

                if (timestamp != null) {
                    _state.update { State.AddTimestamp(id, timestamp) }
                } else {
                    _state.update { State.Finished }
                }
            }
        }

        return videoResult
    }

}