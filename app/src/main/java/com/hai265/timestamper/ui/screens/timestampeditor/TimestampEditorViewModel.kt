package com.hai265.timestamper.ui.screens.timestampeditor

import androidx.lifecycle.ViewModel
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.domain.UpsertTimestampUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class TimestampEditorViewModel @Inject constructor(
    private val upsertTimestampUseCase: UpsertTimestampUseCase,
) : ViewModel() {

    suspend fun upsertTimestamp(timestamp: Timestamp): Uuid {
        return upsertTimestampUseCase.invokeExternalScope(timestamp)
    }
}