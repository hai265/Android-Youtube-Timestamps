package com.hai265.timestamper.data.database.powersync

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hai265.timestamper.data.AppSqlDatabase

class AndroidDatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            AppSqlDatabase.Schema.synchronous(),
            context,
            "appdatabase.db",
            callback = object : AndroidSqliteDriver.Callback(AppSqlDatabase.Schema.synchronous()) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    // Explicitly enable foreign key enforcement
                    db.execSQL("PRAGMA foreign_keys = ON;")
                }
            })
    }
}