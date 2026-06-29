package com.hai265.timestamper.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.AuthRepository
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import com.hai265.timestamper.domain.ImportTimestampsFromFileUseCase
import com.hai265.timestamper.domain.InvalidImportFileException
import com.hai265.timestamper.screens.formatDurationToHHMMSS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlin.uuid.Uuid

private const val TAG = "VideoListScreenViewModel"

sealed interface ListScreenState {
    data object Initial : ListScreenState
    data class Loaded(val videos: List<Video>) : ListScreenState
}

class VideoListScreenViewModel(
    private val repo: VideoRepository,
    private val timestampRepo: TimestampRepository,
    private val authRepository: AuthRepository,
    private val importTimestampsFromFileUseCase: ImportTimestampsFromFileUseCase,
    private val exportTimestampsToFileUseCase: ExportTimestampsToFileUseCase,
) : ViewModel() {
    val state: StateFlow<ListScreenState> =
        combine(repo.getVideos(), authRepository.userId)
        { videos, userId ->
            ListScreenState.Loaded(
                videos = videos.sortedByDescending { it.lastEdited },
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ListScreenState.Initial
            )

    suspend fun addVideo(url: String): VideoResult {
        return repo.addVideo(url)
    }

    fun deleteVideo(video: Video) =
        viewModelScope.launch {
            repo.deleteVideo(video)
        }

    fun exportVideo(sink: Sink) {
        viewModelScope.launch {
            exportTimestampsToFileUseCase.invoke(sink)
        }
        return
    }

    fun importTimestamps(source: Source) {
        viewModelScope.launch {
            try {
                importTimestampsFromFileUseCase.invoke(source)
            } catch (e: InvalidImportFileException) {
                //TODO: toast channel
            }
        }
    }

    suspend fun getTimestampsAsString(videoId: Uuid): String {
        val timestamps = timestampRepo.getTimestamps(videoId).first()
        return timestamps.joinToString(separator = "\n")
        { timestamp -> "${timestamp.time.formatDurationToHHMMSS()} ${timestamp.description}" }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

}