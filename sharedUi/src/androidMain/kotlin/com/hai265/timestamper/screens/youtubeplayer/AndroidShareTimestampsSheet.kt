package com.hai265.timestamper.screens.youtubeplayer

import android.content.Context
import android.content.Intent
import com.hai265.timestamper.screens.ShareTimestampsSheet

class AndroidShareTimestampsSheet(private val context: Context) : ShareTimestampsSheet {
    override suspend fun invoke(timestamps: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                timestamps
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}