plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.example.todoapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.example.todoapp"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
  implementation(libs.lifecycle.livedata.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  implementation(libs.coroutines.android)
  ksp(libs.room.compiler)

  testImplementation(libs.junit)
  testImplementation(libs.arch.core.testing)
  testImplementation(libs.coroutines.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.room.testing)
  androidTestImplementation(libs.coroutines.test)
}
