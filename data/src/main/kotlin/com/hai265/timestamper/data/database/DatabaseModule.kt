package com.hai265.timestamper.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hai265.timestamper.data.database.powersync.powersyncModule
import com.hai265.timestamper.data.kmpDataModule
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val dataModule = module {
    includes(kmpDataModule, powersyncModule)
}