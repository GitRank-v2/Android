plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 34
    namespace = "com.dragonguard.android"
    defaultConfig {
        applicationId = "com.dragonguard.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    implementation(libs.androidx.activity)
    implementation(libs.fragment)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    //browser
    implementation(libs.browser)

    //glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    //viewModel
    implementation(libs.viewmodel)

    //lottie
    implementation(libs.lottie)

    //circleImageView
    implementation(libs.circleimageview)

    //MPAndroidChart
    implementation(libs.mpandroidchart)

    //hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)


    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espreso)
}