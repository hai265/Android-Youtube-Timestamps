import app.cash.sqldelight.db.SqlDriver
import kotlin.time.Instant

val Instant.Companion.ZERO: Instant
    get() = Instant.fromEpochSeconds(0L)

expect fun createSqlDriver(): SqlDriver
