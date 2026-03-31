package com.hai265.timestamper.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.TimestampsRepository
import com.hai265.timestamper.data.repos.VideoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false,
)

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val repo: TimestampsRepository
) : ViewModel() {

    val state = repo.getVideos()
        .map {
            ListScreenState(videos = it, syncing = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ListScreenState(
                syncing = false
            )
        )

    suspend fun addVideo(url: String): VideoResult {
        return repo.addVideo(url)
    }

}