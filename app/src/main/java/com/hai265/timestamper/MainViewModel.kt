package com.hai265.timestamper

import androidx.lifecycle.ViewModel
import com.hai265.timestamper.data.repos.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {
    suspend fun addVideo(url: String) = repo.addVideo(url)
}