package com.hai265.timestamper.domain

import org.koin.dsl.module

val domainModule = module {
    factory { ExportTimestampsToFileUseCase() }
    factory { ImportTimestampsFromFileUseCase(get()) }
    factory { UpsertTimestampUseCase(get(), get(), get()) }
}