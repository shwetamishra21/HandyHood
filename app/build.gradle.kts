plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.handyhood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.handyhood"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        // ✅ SAFE + CORRECT
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${project.findProperty("SUPABASE_URL") as String? ?: ""}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"${project.findProperty("SUPABASE_ANON_KEY") as String? ?: ""}\""
        )
    }

    // ✅ R8 OPTIMIZATION - 30-50% faster + smaller APK
    buildTypes {

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

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
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ✅ SUPABASE - Latest stable
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.5.3")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.3")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.3")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.5.3")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.5.3")

    // ✅ SERIALIZATION - Single version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ✅ COMPOSE BOM - Manages versions automatically
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ NAVIGATION + LIFECYCLE
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // ✅ DATASTORE + NETWORK
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("io.ktor:ktor-client-okhttp:2.3.9")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // ✅ DEBUG TOOLS
    debugImplementation("androidx.compose.ui:ui-tooling")
}
