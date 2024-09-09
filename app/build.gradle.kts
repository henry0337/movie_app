import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    val props = Properties().apply {
        file("../private.properties").inputStream().use { load(it) }
    }

    namespace = "com.henry.movieapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.henry.movieapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // For Windows 11 users, use this line instead of the one below
//            buildConfigField("String", "FIREBASE_URL", props.getProperty("FIREBASE_URL"))
            // For Windows 10 users, this one should be better (I don't test this in any other platforms)
            buildConfigField("String", "FIREBASE_URL", "\"${props.getProperty("FIREBASE_URL")}\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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
    // Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.kotlinx.coroutines.android)

    // GPS library
    implementation(libs.play.services.auth)

    // Credential Manager libraries
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Firebase libraries
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    // Koin DI libraries
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    // 3rd UI libraries
    implementation(libs.glide)
    implementation(libs.chip.navigation.bar)
    implementation(libs.blurview)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}