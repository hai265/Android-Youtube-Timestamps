package com.hai265.timestamper.data.database.powersync

import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val powersyncModule: Module = module {
    single<PowerSyncDatabase> {
        val context = androidContext()
        val driverFactory = DatabaseDriverFactory(context)

        PowerSyncDatabase(
            factory = driverFactory,
            schema = schema,
            dbFilename = "app_database"
        )
    }
}