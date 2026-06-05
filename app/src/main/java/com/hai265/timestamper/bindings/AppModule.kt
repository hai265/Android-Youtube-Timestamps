package com.hai265.timestamper.bindings

import android.content.ContentResolver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<ContentResolver> {
        androidContext().contentResolver
    }
}