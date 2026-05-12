package com.hai265.timestamper.ui.screens.timestampeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.domain.UpsertTimestampUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimestampEditorViewModel @Inject constructor(
    private val upsertTimestampUseCase: UpsertTimestampUseCase,
) : ViewModel() {

    fun upsertTimestamp(timestamp: Timestamp) {
        viewModelScope.launch {
            upsertTimestampUseCase.invoke(timestamp)
        }
    }
}