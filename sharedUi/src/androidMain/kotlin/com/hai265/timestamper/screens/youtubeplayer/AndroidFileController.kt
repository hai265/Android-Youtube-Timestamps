package com.hai265.timestamper.screens.youtubeplayer

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.hai265.timestamper.screens.FileController
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.io.IOException
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlin.coroutines.resumeWithException

class AndroidFileController(
    private val activity: ComponentActivity
) : FileController {

    private var createContinuation: CancellableContinuation<Sink>? = null
    private var openContinuation: CancellableContinuation<Source>? = null

    // Register the Create Document picker (Allows renaming and choosing directory)
    private val createFileLauncher = activity.registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val continuation = createContinuation
        createContinuation = null // Prevent memory leaks

        if (uri != null) {
            val outputStream = activity.contentResolver.openOutputStream(uri)
                ?: throw IOException("Failed to open output stream for URI: $uri")
            continuation?.resume(
                outputStream.asSink().buffered()
            ) { cause, _, _ -> continuation.resumeWithException(cause) }

        }
    }

    // Register the Open Document picker (Allows selecting an existing JSON file)
    private val openFileLauncher = activity.registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        val continuation = openContinuation
        openContinuation = null // Prevent memory leaks

        if (uri != null) {
            val inputStream = activity.contentResolver.openInputStream(uri)
                ?: throw IOException("Failed to open input stream for URI: $uri")
            continuation?.resume(
                inputStream.asSource().buffered()
            ) { cause, _, _ -> continuation.resumeWithException(cause) }

        }
    }

    override suspend fun createFile(fileName: String): Sink =
        suspendCancellableCoroutine { continuation ->
            createContinuation = continuation
            try {
                // Launches the UI and suggests the initial filename
                createFileLauncher.launch(fileName)
            } catch (e: Exception) {
                createContinuation = null
                continuation.resumeWithException(e)
            }
        }

    override suspend fun openFilePicker(): Source = suspendCancellableCoroutine { continuation ->
        openContinuation = continuation
        try {
            // Launches the UI and filters strictly for JSON files
            openFileLauncher.launch(arrayOf("application/json"))
        } catch (e: Exception) {
            openContinuation = null
            continuation.resumeWithException(e)
        }
    }
}