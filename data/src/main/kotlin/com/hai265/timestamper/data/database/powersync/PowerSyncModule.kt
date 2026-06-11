package com.hai265.timestamper.data.database.powersync

import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val powersyncModule = module {
    single<PowerSyncDatabase> {
        val context = androidContext()
        val driverFactory = DatabaseDriverFactory(context)

        PowerSyncDatabase(
            factory = driverFactory,
            schema = schema,
            dbFilename = "app_database"
        )
    }

    single<PowerSyncBackendConnector> { get<SupabaseConnector>() }
}