plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

val notesApiUrl = providers.gradleProperty("notesApiUrl").getOrElse("http://10.0.2.2:8081")

android {
  namespace = "com.example.notesapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.example.notesapp"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
    buildConfigField("String", "API_BASE_URL", "\"$notesApiUrl\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(libs.core.ktx)
  implementation(libs.activity.compose)
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.core)
  implementation(libs.compose.ui.tooling.preview)
  debugImplementation(libs.compose.ui.tooling)
  implementation(libs.lifecycle.runtime.compose)
  implementation(libs.lifecycle.viewmodel.compose)
  implementation(libs.navigation3.runtime)
  implementation(libs.navigation3.ui)
  implementation(libs.lifecycle.viewmodel.navigation3)
  implementation(libs.kotlinx.serialization.json)
  testImplementation(libs.junit)
}
