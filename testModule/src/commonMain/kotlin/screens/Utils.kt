package screens

import kotlin.time.Duration

fun Duration.formatDurationToHHMMSS(): String =
    this.toComponents { hours, minutes, seconds, _ ->
        if (hours >= 1L) {
            "$hours:$minutes:$seconds"
        } else {
            "$minutes:$seconds"
        }
    }