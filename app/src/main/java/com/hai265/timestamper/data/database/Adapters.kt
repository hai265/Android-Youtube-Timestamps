package com.hai265.timestamper.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Duration
import kotlin.time.Instant

val instantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String): Instant =
        Instant.parse(databaseValue)

    override fun encode(value: Instant): String =
        value.toString() // ISO-8601: "2024-01-15T10:30:00Z"
}

val durationAdapter = object : ColumnAdapter<Duration, String> {
    override fun decode(databaseValue: String): Duration =
        Duration.parseIsoString(databaseValue)

    override fun encode(value: Duration): String =
        value.toIsoString() // ISO-8601: "PT1H30M"
}