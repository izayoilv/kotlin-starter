plugins {
  alias(libs.plugins.android.application)
}

android {
  namespace = "com.example.newsreader"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.example.newsreader"
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
  implementation(libs.recyclerview)
  implementation(libs.constraintlayout)
  implementation(libs.material)
  implementation(libs.navigation.fragment.ktx)
  implementation(libs.navigation.ui.ktx)
  testImplementation(libs.junit)
}
