plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.owasp.dependencycheck")
}

android {
    namespace = "com.betting.manager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.betting.manager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Room schema location
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Signing configuration from environment variables
            signingConfig = signingConfigs.create("release") {
                storeFile = file("keystore.jks")
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
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
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.01")
    implementation(composeBom)
    
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("androidx.room:room-testing:2.6.1")
    
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// Configure dependency check
dependencyCheck {
    format = "ALL"
    suppressionFile = "$projectDir/dependency-check-suppressions.xml"
    failBuildOnCVSS = 7.0f
    skipConfigurations = listOf("lintClassPath")
}

// Create a task to generate keystore for CI/CD
tasks.register("generateKeystore") {
    doLast {
        val keystoreFile = file("keystore.jks")
        if (!keystoreFile.exists()) {
            project.exec {
                commandLine(
                    "keytool",
                    "-genkey",
                    "-v",
                    "-keystore", keystoreFile.absolutePath,
                    "-alias", "bettingmanager",
                    "-keyalg", "RSA",
                    "-keysize", "2048",
                    "-validity", "10000",
                    "-storepass", "android",
                    "-keypass", "android",
                    "-dname", "CN=Betting Manager, OU=Development, O=Betting Inc, L=San Francisco, ST=California, C=US"
                )
            }
            println("Keystore generated at: ${keystoreFile.absolutePath}")
        } else {
            println("Keystore already exists at: ${keystoreFile.absolutePath}")
        }
    }
}

// Task to encode keystore for GitHub Secrets
tasks.register("encodeKeystore") {
    doLast {
        val keystoreFile = file("keystore.jks")
        if (keystoreFile.exists()) {
            project.exec {
                commandLine("base64", "-w", "0", keystoreFile.absolutePath)
            }
            println("\n\nCopy the base64 output above and save it as KEYSTORE_BASE64 GitHub Secret")
            println("Also set these GitHub Secrets:")
            println("KEYSTORE_PASSWORD: android")
            println("KEY_ALIAS: bettingmanager")
            println("KEY_PASSWORD: android")
        } else {
            println("Keystore not found. Run './gradlew generateKeystore' first.")
        }
    }
}