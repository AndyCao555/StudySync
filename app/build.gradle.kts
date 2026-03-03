import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.kotlin_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kotlin_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Load Supabase credentials from local.properties
        // Read file manually since Properties.load() seems to have issues
        val localPropertiesFile = rootProject.file("local.properties")
        var supabaseUrl = "https://your-project-id.supabase.co"
        var supabaseAnonKey = ""
        
        if (localPropertiesFile.exists()) {
            println("✅ Reading local.properties from: ${localPropertiesFile.absolutePath}")
            try {
                val lines = localPropertiesFile.readLines()
                println("   Total lines in file: ${lines.size}")
                lines.forEachIndexed { index, line ->
                    val trimmed = line.trim()
                    println("   Line ${index + 1}: '$trimmed'")
                    // Skip comments and empty lines
                    if (!trimmed.startsWith("#") && trimmed.contains("=")) {
                        val parts = trimmed.split("=", limit = 2)
                        if (parts.size == 2) {
                            val key = parts[0].trim()
                            val value = parts[1].trim()
                            println("   Parsed: key='$key', value='${value.take(30)}...'")
                            when (key) {
                                "SUPABASE_URL" -> {
                                    supabaseUrl = value
                                    println("   ✅ Found SUPABASE_URL: $value")
                                }
                                "SUPABASE_ANON_KEY" -> {
                                    supabaseAnonKey = value
                                    println("   ✅ Found SUPABASE_ANON_KEY (length: ${value.length})")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("⚠️ Error reading local.properties: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("⚠️ local.properties not found at: ${rootProject.file("local.properties").absolutePath}")
        }
        
        println("📝 Final Supabase URL: $supabaseUrl")
        println("📝 Final Supabase Key length: ${supabaseAnonKey.length}")
        
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.kotlinx.serialization.json)
    // Ktor Android client engine (required for Supabase HTTP client)
    implementation("io.ktor:ktor-client-android:2.3.12")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
