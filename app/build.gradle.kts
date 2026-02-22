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
    val composeBom = platform(libs.findLibrary("compose-bom").get())
    implementation(composeBom)
    
    // Android Core
    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    implementation(libs.findLibrary("androidx-activity-compose").get())
    
    // Compose UI
    implementation(libs.findLibrary("compose-ui").get())
    implementation(libs.findLibrary("compose-ui-graphics").get())
    implementation(libs.findLibrary("compose-ui-tooling-preview").get())
    implementation(libs.findLibrary("compose-material3").get())
    implementation(libs.findLibrary("compose-material-icons-extended").get())
    
    // Navigation
    implementation(libs.findLibrary("compose-navigation").get())
    
    // ViewModel
    implementation(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
    
    // Room Database
    implementation(libs.findLibrary("room-runtime").get())
    implementation(libs.findLibrary("room-ktx").get())
    kapt(libs.findLibrary("room-compiler").get())
    
    // Hilt
    implementation(libs.findLibrary("hilt-android").get())
    kapt(libs.findLibrary("hilt-compiler").get())
    implementation(libs.findLibrary("androidx-hilt-navigation-compose").get())
    
    // Coroutines
    implementation(libs.findLibrary("kotlinx-coroutines-android").get())
    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
    
    // Serialization
    implementation(libs.findLibrary("kotlinx-serialization-json").get())
    
    // DataStore
    implementation(libs.findLibrary("androidx-datastore-preferences").get())
    
    // Coil for image loading
    implementation(libs.findLibrary("coil-compose").get())
    
    // Testing
    testImplementation(libs.findLibrary("junit").get())
    testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
    testImplementation(libs.findLibrary("androidx-test-core").get())
    testImplementation(libs.findLibrary("androidx-test-ext-junit").get())
    testImplementation(libs.findLibrary("mockk").get())
    testImplementation(libs.findLibrary("room-testing").get())
    
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.findLibrary("androidx-test-ext-junit").get())
    androidTestImplementation(libs.findLibrary("androidx-test-espresso-core").get())
    androidTestImplementation(libs.findLibrary("compose-ui-test-junit4").get())
    
    debugImplementation(libs.findLibrary("compose-ui-tooling").get())
    debugImplementation(libs.findLibrary("compose-ui-test-manifest").get())
}

// Configure dependency check
dependencyCheck {
    format = "ALL"
    suppressionFile = file("$projectDir/dependency-check-suppressions.xml")
    failBuildOnCVSS = 7.0f
    skipConfigurations = listOf("lintClassPath")
}

// Create a task to generate keystore for CI/CD
tasks.register("generateKeystore") {
    doLast {
        val keystoreFile = file("keystore.jks")
        if (!keystoreFile.exists()) {
            exec {
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
            exec {
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