import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

val localProps = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
//    alias(libs.plugins.google.services)
    alias(libs.plugins.sqldelight)
}

extensions.configure<ApplicationExtension> {
    namespace = "com.hai265.timestamper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hai265.timestamper"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.coil3.compose)
    implementation(libs.coil3.okhttp3)
    implementation(libs.okhttp3)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.hilt)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidyoutubeplayer)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.material3)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.core)
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.firestore)
    implementation(libs.powersync.core)
    implementation(libs.powersync.room)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.powersync.integration.supabase)
    implementation(libs.postgrest.kt)
    implementation(libs.kotlinx.io.core)
    implementation(libs.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.integration.sqldelight)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.android.driver)
    androidTestImplementation(libs.turbine)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.sqllite.driver)
}
