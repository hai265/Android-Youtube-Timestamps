package com.hai265.timestamper.data.database.powersync

import android.content.Context
import com.hai265.timestamper.BuildConfig
import com.hai265.timestamper.data.database.powersync.SupabaseConnector
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PowerSyncModule {
    companion object {
        @Provides
        @Singleton
        fun providesPowerSyncDatabase(
            @ApplicationContext context: Context
        ): PowerSyncDatabase {
            val driverFactory = DatabaseDriverFactory(context)

            val powersyncDatabase = PowerSyncDatabase(
                factory = driverFactory,
                schema = schema,
                dbFilename = "app_database"
            )
            return powersyncDatabase
        }

        @Provides
        @Singleton
        fun providesPowerSyncConnector(connector: SupabaseConnector): PowerSyncBackendConnector {
            return connector
        }

        @Provides
        @Singleton
        fun providesSupabaseConnector(): SupabaseConnector {
            return SupabaseConnector(
                powerSyncEndpoint = BuildConfig.POWERSYNC_ENDPOINT,
                supabaseUrl = BuildConfig.SUPABASE_ENDPOINT,
                supabaseKey = BuildConfig.SUPABASE_KEY,
                storageBucket = BuildConfig.SUPABASE_STORAGE_BUCKET
            )
        }
    }
}