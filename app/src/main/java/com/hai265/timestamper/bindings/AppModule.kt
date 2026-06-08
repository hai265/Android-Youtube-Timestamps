package com.hai265.timestamper.bindings

import android.content.ContentResolver
import com.hai265.timestamper.data.database.dataModule
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import com.hai265.timestamper.domain.ImportTimestampsFromFileUseCase
import com.hai265.timestamper.domain.TestUseCase
import com.hai265.timestamper.domain.UpsertTimestampUseCase
import com.hai265.timestamper.ui.screens.editor.TimestampViewerViewModel
import com.hai265.timestamper.ui.screens.list.VideoListScreenViewModel
import com.hai265.timestamper.ui.screens.signin.AuthViewModel
import com.hai265.timestamper.ui.screens.test.TestViewModel
import com.hai265.timestamper.ui.screens.timestampeditor.TimestampEditorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(dataModule)
    single<ContentResolver> {
        androidContext().contentResolver
    }
    factory { ExportTimestampsToFileUseCase() }
    factory { ImportTimestampsFromFileUseCase(get()) }
    single { UpsertTimestampUseCase(get(), get(), get()) }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
    viewModel {
        TestViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        AuthViewModel(get())
    }
    viewModel {
        VideoListScreenViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        TimestampViewerViewModel(get(), get(), get(), get())
    }
    viewModel<TimestampEditorViewModel> {
        TimestampEditorViewModel(get())
    }
    factory {
        TestUseCase(videoRepository = get())
    }
}