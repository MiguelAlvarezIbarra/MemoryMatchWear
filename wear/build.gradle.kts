plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.utng.memorymatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "mx.utng.memorymatch"
        minSdk = 30  // Wear OS 3.0+
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose for Wear OS
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.foundation)

    // Animaciones (flip de tarjetas)
    implementation(libs.androidx.compose.animation)

    // ViewModel + Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // DataStore (mejor tiempo)
    implementation(libs.androidx.datastore.preferences)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.wear.core.splashscreen)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
}
