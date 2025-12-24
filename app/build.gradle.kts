plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.group18"
    // <<< UPDATED: Changed from 34 to 36, as required by your dependencies.
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.group18"
        minSdk = 28
        // <<< UPDATED: It's best practice to target the same SDK version you compile against.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// --- ALL DEPENDENCIES GO INSIDE THIS ONE BLOCK ---
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// Optional: Logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Add the Glide dependency here
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
