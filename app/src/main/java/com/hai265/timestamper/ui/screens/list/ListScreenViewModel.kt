package com.hai265.timestamper.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.TimestampsRepository
import com.hai265.timestamper.data.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false
)

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val repo: TimestampsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ListScreenState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(syncing = true) }
            _state.update { it.copy(videos = repo.getVideos(), syncing = false) }
        }
    }

}