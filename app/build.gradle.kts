import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.olanavigationnew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.olanavigationnew"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())

            val clientId = localProperties.getProperty("CLIENT_ID")
            val clientSecret = localProperties.getProperty("CLIENT_SECRET")
            val apiKey = localProperties.getProperty("API_KEY")

            buildConfigField("String", "CLIENT_ID", "\"${clientId}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${clientSecret}\"")
            buildConfigField("String", "API_KEY", "\"${apiKey}\"")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(files("libs/mapsnavigationsdk.aar"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation (libs.okhttpprofiler)

    //Required for OlaMap SDK
    implementation("org.maplibre.gl:android-sdk:10.2.0")
    implementation("org.maplibre.gl:android-sdk-directions-models:5.9.0")
    implementation("org.maplibre.gl:android-sdk-services:5.9.0")
    implementation("org.maplibre.gl:android-sdk-turf:5.9.0")
    implementation("org.maplibre.gl:android-plugin-markerview-v9:1.0.0")
    implementation("org.maplibre.gl:android-plugin-annotation-v9:1.0.0")
    implementation("com.moengage:moe-android-sdk:12.6.01")

// Used in sample app
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    implementation("androidx.lifecycle:lifecycle-compiler:2.0.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation ("com.squareup.retrofit2:converter-moshi:2.11.0")

}