package com.hai265.timestamper.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import app.cash.sqldelight.db.SqlDriver
import com.hai265.timestamper.data.database.powersync.powersyncModule
import com.hai265.timestamper.data.kmpDataModule
import com.hai265.timestamper.data.repos.AuthRepository
import com.powersync.integrations.sqldelight.PowerSyncDriver
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val dataModule = module {
    includes(kmpDataModule, powersyncModule)
    single<SqlDriver> {
        PowerSyncDriver(
            db = get(),
            scope = get()
        )
    }

    single { AuthRepository(get(), get()) }
}