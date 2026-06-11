package com.hai265.timestamper.data.database.powersync

import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val powersyncModule: Module = module {
    single<PowerSyncDatabase> {
        val driverFactory = DatabaseDriverFactory()

        PowerSyncDatabase(
            factory = driverFactory,
            schema = schema,
            dbFilename = "app_database"
        )
    }
}