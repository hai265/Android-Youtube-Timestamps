package com.hai265.timestamper.domain

import org.koin.dsl.module

val domainModule = module {
    factory { ExportTimestampsToFileUseCase(get()) }
    factory { ImportTimestampsFromFileUseCase(get(), get()) }
    factory { UpsertTimestampUseCase(get(), get(), get()) }
}