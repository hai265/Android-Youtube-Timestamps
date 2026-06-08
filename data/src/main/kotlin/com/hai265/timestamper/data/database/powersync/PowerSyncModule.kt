package com.hai265.timestamper.data.database.powersync

import com.hai265.timestamper.data.BuildConfig
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
    single<SupabaseConnector> {
        SupabaseConnector(
            powerSyncEndpoint = BuildConfig.POWERSYNC_ENDPOINT,
            supabaseUrl = BuildConfig.SUPABASE_ENDPOINT,
            supabaseKey = BuildConfig.SUPABASE_KEY,
            storageBucket = BuildConfig.SUPABASE_STORAGE_BUCKET
        )
    }

    single<PowerSyncBackendConnector> { get<SupabaseConnector>() }
}