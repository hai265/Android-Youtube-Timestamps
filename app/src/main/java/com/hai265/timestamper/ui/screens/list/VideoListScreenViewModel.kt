package com.hai265.timestamper.ui.screens.list

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.AuthRepository
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import com.hai265.timestamper.domain.ImportTimestampsFromFileUseCase
import com.hai265.timestamper.ui.screens.editor.formatDurationToHHMMSS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

private const val TAG = "VideoListScreenViewModel"

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false,
)

@HiltViewModel
class VideoListScreenViewModel @Inject constructor(
    private val repo: VideoRepository,
    private val timestampRepo: TimestampRepository,
    private val authRepository: AuthRepository,
    private val importTimestampsFromFileUseCase: ImportTimestampsFromFileUseCase,
    private val exportTimestampsToFileUseCase: ExportTimestampsToFileUseCase,
) : ViewModel() {

    val state = repo.getVideos()
        .map { videos ->
            ListScreenState(videos = videos.sortedByDescending { it.lastEdited }, syncing = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ListScreenState(
                syncing = false
            )
        )

    suspend fun addVideo(url: String): VideoResult {
        return repo.addVideo(url, authRepository.userId.value)
    }

    fun deleteVideo(video: Video) =
        viewModelScope.launch {
            repo.deleteVideo(video)
        }

    fun exportVideo(uri: Uri) {
        viewModelScope.launch {
            exportTimestampsToFileUseCase.invoke(uri)
        }
        return
    }

    fun importTimestamps(uri: Uri) {
        viewModelScope.launch {
            importTimestampsFromFileUseCase.invoke(uri)
        }
    }

    suspend fun getTimestampsAsString(videoId: Uuid): String {
        val timestamps = timestampRepo.getTimestamps(videoId).first()
        return timestamps.joinToString(separator = "\n")
        { timestamp -> "${timestamp.time.formatDurationToHHMMSS()} ${timestamp.description}" }
    }

}