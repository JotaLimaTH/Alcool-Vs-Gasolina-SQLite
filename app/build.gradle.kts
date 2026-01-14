plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

}

android {
    namespace = "com.example.tarefa2progmobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tarefa2progmobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}


dependencies {
    val sqlite_version = "2.6.2"
    // Java language implementation
    implementation("androidx.sqlite:sqlite:${sqlite_version}")
    // Kotlin
    implementation("androidx.sqlite:sqlite-ktx:${sqlite_version}")
    // Implementation of the AndroidX SQLite interfaces via the Android framework APIs.

    implementation("androidx.sqlite:sqlite-framework:${sqlite_version}")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.play.services.location)
    implementation(libs.androidx.ui)
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}
