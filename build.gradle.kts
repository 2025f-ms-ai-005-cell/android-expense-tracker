plugins {
    id("com.android.application") version "8.13.2"
    id("org.jetbrains.kotlin.android") version "2.2.20"
}
android {
    namespace = "dev.sadia.pocketledger"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.sadia.pocketledger"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    sourceSets {
        getByName("main") { manifest.srcFile("AndroidManifest.xml"); java.setSrcDirs(listOf("src")) }
        getByName("test") { java.setSrcDirs(listOf("tests")) }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies { testImplementation("junit:junit:4.13.2") }
