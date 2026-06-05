import java.util.Properties

val localProps = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hai265.timestamper.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            buildConfigField(
                "String", "POWERSYNC_TOKEN",
                "\"${localProps["powersync.dev.token"]}\""
            )
            buildConfigField(
                "String", "POWERSYNC_ENDPOINT",
                "\"${localProps["powersync.endpoint"]}\""
            )
            buildConfigField(
                "String", "SUPABASE_ENDPOINT",
                "\"${localProps["supabase.endpoint"]}\""
            )
            buildConfigField(
                "String", "SUPABASE_KEY",
                "\"${localProps["supabase.key"]}\""
            )
            buildConfigField(
                "String", "SUPABASE_STORAGE_BUCKET",
                "\"${localProps["supabase.storage.bucket"]}\""
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}

sqldelight {
    databases {
        linkSqlite.set(false)
        create("AppSqlDatabase") {
            generateAsync.set(true)
            deriveSchemaFromMigrations.set(false)
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")
        }
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.okhttp3)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.hilt)
    implementation(libs.core.ktx)
    ksp(libs.hilt.android.compiler)
    testImplementation(libs.junit)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.powersync.core)
    implementation(libs.powersync.room)
    implementation(libs.powersync.integration.supabase)
    implementation(libs.postgrest.kt)
    implementation(libs.kotlinx.io.core)
    implementation(libs.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.integration.sqldelight)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.sqllite.driver)
}