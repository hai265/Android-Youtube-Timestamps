package screens.timestampeditor

import androidx.lifecycle.ViewModel
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.domain.UpsertTimestampUseCase
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TimestampEditorViewModel(
    private val upsertTimestampUseCase: UpsertTimestampUseCase,
) : ViewModel() {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun upsertTimestamp(timestamp: Timestamp): Uuid {
        return upsertTimestampUseCase.invokeExternalScope(timestamp)
    }
}