package deps

import com.hai265.timestamper.screens.FileController
import kotlinx.io.Sink
import kotlinx.io.Source

class IosFileController : FileController {
    override suspend fun createFile(fileName: String): Sink {
        TODO("Not yet implemented")
    }

    override suspend fun openFilePicker(): Source {
        TODO("Not yet implemented")
    }
}