package com.hai265.timestamper.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module

// shared/src/commonMain/kotlin/createDataStore.kt

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 */

fun createDataStore(storage: Storage<Preferences>): DataStore<Preferences> =
    DataStoreFactory.create(storage = storage)

internal const val dataStoreFileName = "settings.preferences_pb"

expect val preferencesModule: Module
