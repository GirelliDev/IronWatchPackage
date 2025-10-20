plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.girellidev.ironwatchmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.girellidev.ironwatchmobile"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // apenas dependências básicas do Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.11.0") // já tá, mas garante
    implementation("androidx.activity:activity-ktx:1.9.0") // útil pra Material3
    implementation("androidx.appcompat:appcompat:1.7.0") // pode manter
}
