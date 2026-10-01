import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val isAiStudio = System.getenv("ANDROID_FRAMEWORK") == "true"

plugins {
    id("com.android.application")
}

if (!isAiStudio) {
    apply(plugin = "org.jetbrains.kotlin.android")
}
apply(plugin = "org.jetbrains.kotlin.plugin.compose")
apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.dagger.hilt.android")
apply(plugin = "com.google.devtools.ksp")

android {
    namespace = "com.saiful.findbackbd"
    compileSdk = if (isAiStudio) 36 else 35

    val envProperties = Properties()
    val envFile = rootProject.file(".env")
    if (envFile.exists()) envFile.inputStream().use { envProperties.load(it) }
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) localPropsFile.inputStream().use { envProperties.load(it) }

    val mapsApiKey = envProperties.getProperty("MAPS_API_KEY")
        ?: System.getenv("MAPS_API_KEY")
        ?: ""

    defaultConfig {
        applicationId = "com.saiful.findbackbd"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        minSdk = 24
        targetSdk = if (isAiStudio) 36 else 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    val coreKtxVersion = if (isAiStudio) "1.17.0" else "1.15.0"
    val activityComposeVersion = if (isAiStudio) "1.10.1" else "1.9.3"
    val navVersion = if (isAiStudio) "2.8.9" else "2.8.5"
    val firebaseBomVersion = if (isAiStudio) "34.17.0" else "33.7.0"
    val coroutinesVersion = if (isAiStudio) "1.10.2" else "1.9.0"
    val hiltVersion = if (isAiStudio) "2.59.2" else "2.51.1"
    val roomVersion = if (isAiStudio) "2.7.0" else "2.6.1"
    val retrofitVersion = if (isAiStudio) "2.12.0" else "2.11.0"
    val mapsComposeVersion = if (isAiStudio) "6.12.2" else "6.2.1"

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    "ksp"("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    "ksp"("androidx.room:room-compiler:$roomVersion")

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")

    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
