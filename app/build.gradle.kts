
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aiva.assistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aiva.assistant"
        minSdk = 26
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}
