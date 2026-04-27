package com.hai265.timestamper.ui.screens.list

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.getYouTubeId
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.domain.TimestampsToYamlStringUseCase
import com.hai265.timestamper.domain.YamlToTimestampsUseCase
import com.hai265.timestamper.ui.screens.editor.formatDurationToHHMMSS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VideoListScreenViewModel"

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false,
)

@HiltViewModel
class VideoListScreenViewModel @Inject constructor(
    private val repo: VideoRepository,
    private val timestampsToYamlStringUseCase: TimestampsToYamlStringUseCase,
    private val yamlToTimestampsUseCase: YamlToTimestampsUseCase,
    private val timestampRepo: TimestampRepository,
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
        val videoId = getYouTubeId(url) ?: return VideoResult.InvalidUrl(url)
        return repo.addVideo(videoId)
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

    fun importTimestamps(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            //Android doesn't seem to support yaml types
            Log.d(TAG, "import type: ${contentResolver.getType(uri)}")
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val content = inputStream.bufferedReader().use { it.readText() }
                yamlToTimestampsUseCase.invoke(content)
            }
        }
    }

    suspend fun getTimestampsAsString(videoId: String): String {
        val timestamps = timestampRepo.getTimestamps(videoId).first()
        return timestamps.joinToString(separator = "\n")
        { timestamp -> "${timestamp.time.formatDurationToHHMMSS()} ${timestamp.description}" }
    }

}