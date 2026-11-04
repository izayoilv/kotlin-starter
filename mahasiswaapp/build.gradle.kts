plugins {
  alias(libs.plugins.android.application)
}

val apiBaseUrl = providers.gradleProperty("apiBaseUrl")
  .getOrElse("http://10.0.2.2:8080/")

android {
  namespace = "com.example.mahasiswaapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.example.mahasiswaapp"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
    buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
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
    viewBinding = true
    buildConfig = true
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  implementation(libs.appcompat)
  implementation(libs.core.ktx)
  implementation(libs.activity.ktx)
  implementation(libs.recyclerview)
  implementation(libs.material)
  implementation(libs.swiperefreshlayout)
  implementation(libs.lifecycle.viewmodel.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.gson)
  implementation(libs.okhttp)
  implementation(libs.coroutines.android)
}
