plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"

}

android {
    namespace = "com.example.amilimetros"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.amilimetros"
        minSdk = 24
        targetSdk = 35
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
    // --- Core AndroidX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended:1.7.5")

    // --- Navigation y ViewModel Compose ---
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // --- Corrutinas ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // --- Room (Base de datos local SQLite) ---
  //  implementation("androidx.room:room-runtime:2.6.1")
   // implementation("androidx.room:room-ktx:2.6.1")
   // ksp("androidx.room:room-compiler:2.6.1")
//
    // --- DataStore (para guardar preferencias del usuario) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Coil (carga de imágenes en Compose) ---
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ========== CÁMARA Y GALERÍA ==========
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ========== PERMISOS ==========
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ========== MAPAS (Google Maps Compose) ==========
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    // Animations for Jetpack Compose (fadeIn, AnimatedVisibility, etc.)
    implementation("androidx.compose.animation:animation:1.6.0") // o la versión de compose que uses


    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")



    // Retrofit (HTTP Client)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (Logging de requests)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson (JSON parsing)
    implementation("com.google.code.gson:gson:2.10.1")

    // Coil (para cargar imágenes desde URLs)
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}