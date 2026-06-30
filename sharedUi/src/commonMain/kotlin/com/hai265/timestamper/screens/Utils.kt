package com.hai265.timestamper.screens

import androidx.compose.runtime.saveable.Saver
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Duration.formatDurationToHHMMSS(): String =
    toComponents { hours, minutes, seconds, _ ->
        val mm = minutes.toString().padStart(2, '0')
        val ss = seconds.toString().padStart(2, '0')
        if (hours >= 1L) {
            val hh = hours.toString().padStart(2, '0')
            "$hh:$mm:$ss"
        } else {
            "$mm:$ss"
        }
    }

val durationSaver = Saver<Duration, Int>(
    save = { it.toInt(DurationUnit.MILLISECONDS) },
    restore = { it.toDuration(DurationUnit.MILLISECONDS) }
)