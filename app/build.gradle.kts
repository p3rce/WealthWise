plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.wealthwise"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wealthwise"
        minSdk = 24
        targetSdk = 34
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


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation (libs.room.runtime.v240)
    annotationProcessor (libs.room.compiler.v240)


    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")



}