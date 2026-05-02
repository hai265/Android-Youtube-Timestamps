package com.hai265.timestamper.common

import kotlin.time.Instant

val Instant.Companion.ZERO: Instant
    get() = Instant.fromEpochSeconds(0L)