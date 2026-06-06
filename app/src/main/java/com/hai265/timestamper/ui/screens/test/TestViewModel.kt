package com.hai265.timestamper.ui.screens.test

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import org.koin.core.annotation.KoinViewModel

private const val TAG = "VideoListScreenViewModel"

data class ListScreenState(
    val videos: List<Video> = listOf(),
    val syncing: Boolean = false,
    val isLoggedIn: Boolean = false,
)

@KoinViewModel
class TestViewModel(
    private val exportTimestampsFromFileUseCase: ExportTimestampsToFileUseCase,
//    private val importTimestampsFromFileUseCase: ImportTimestampsFromFileUseCase,
) : ViewModel() {


    suspend fun export(): String {
        return exportTimestampsFromFileUseCase.invoke("content://com.android.providers.downloads.documents/document/427".toUri())
    }
}