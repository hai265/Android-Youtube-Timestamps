package com.hai265.timestamper.ui.screens.list

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.domain.TimestampsToYamlStringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false,
)

@HiltViewModel
class VideoListScreenViewModel @Inject constructor(
    private val repo: VideoRepository,
    private val timestampsToYamlStringUseCase: TimestampsToYamlStringUseCase,
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

    fun deleteVideo(video: Video) =
        viewModelScope.launch {
            repo.deleteVideo(video)
        }

    fun exportVideo(videoId: String, uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val content = timestampsToYamlStringUseCase.invoke(videoId)
            contentResolver.openOutputStream(uri)?.use { writer ->
                writer.write(content.toByteArray())
            }
        }
        return
    }

}