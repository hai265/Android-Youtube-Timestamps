package com.hai265.timestamper.bindings

import android.content.ContentResolver
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import com.hai265.timestamper.ui.screens.test.TestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<ContentResolver> {
        androidContext().contentResolver
    }
    factory<ExportTimestampsToFileUseCase>()
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
    viewModel<TestViewModel>()
}