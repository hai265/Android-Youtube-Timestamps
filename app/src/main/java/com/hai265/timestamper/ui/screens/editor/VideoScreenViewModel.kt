package com.hai265.timestamper.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hai265.timestamper.data.TimestampsRepository
import com.hai265.timestamper.data.Video
import com.hai265.timestamper.ui.Navigables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListScreenState(
    val video: Video? = null

)

@HiltViewModel
class VideoScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: TimestampsRepository
) : ViewModel() {
    private val id = savedStateHandle.toRoute<Navigables.VideoScreen>().id
    private val _state = MutableStateFlow(ListScreenState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(video = repo.getVideoById(id)) }
        }
    }

}