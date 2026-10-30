plugins {
  alias(libs.plugins.android.application)
}

android {
  namespace = "com.example.postsapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.example.postsapp"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
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
  implementation(libs.fragment.ktx)
  implementation(libs.recyclerview)
  implementation(libs.material)
  implementation(libs.lifecycle.viewmodel.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.navigation.fragment.ktx)
  implementation(libs.navigation.ui.ktx)
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.gson)
  implementation(libs.coroutines.android)

  testImplementation(libs.junit)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.mockwebserver)
}
