package com.hai265.timestamper.bindings

import android.content.ContentResolver
import com.hai265.timestamper.data.dataModule
import com.hai265.timestamper.domain.domainModule
import com.hai265.timestamper.screens.signin.AuthViewModel
import com.hai265.timestamper.screens.timestampeditor.TimestampEditorViewModel
import com.hai265.timestamper.ui.screens.editor.TimestampViewerViewModel
import com.hai265.timestamper.ui.screens.list.VideoListScreenViewModel
import com.hai265.timestamper.ui.screens.timestampeditor.TimestampDialogActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, domainModule)
    single<ContentResolver> {
        androidContext().contentResolver
    }

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
    viewModel {
        AuthViewModel(get())
    }
    viewModel {
        VideoListScreenViewModel(get(), get(), get(), get(), get(), get())
    }
    viewModel {
        TimestampViewerViewModel(get(), get(), get(), get())
    }
    viewModel {
        TimestampEditorViewModel(get())
    }
    viewModel {
        TimestampDialogActivityViewModel(get())
    }
}