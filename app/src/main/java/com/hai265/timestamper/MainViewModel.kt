package com.hai265.timestamper

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

data class AddTimestamp(
    val videoId: String,
    val time: Duration
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {

    private val _addTimestamp = MutableStateFlow<AddTimestamp?>(null)
    val addTimestamp = _addTimestamp.asStateFlow()

    suspend fun addVideo(url: String): VideoResult {
        val videoResult = repo.addVideo(url)
        when (videoResult) {
            is VideoResult.InvalidUrl -> TODO()
            is VideoResult.NetworkError -> TODO()
            is VideoResult.Success -> {
                val id = videoResult.videoId
                val timestamp = getYoutubeTimestampFromUrl(url)

                if (timestamp != null) {
                    _addTimestamp.update { AddTimestamp(id, timestamp) }
                }
            }
        }

        return videoResult
    }

}