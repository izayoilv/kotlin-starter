plugins {
  alias(libs.plugins.kotlin.jvm)
  application
}

kotlin {
  jvmToolchain(21)
}

application {
  mainClass.set("com.example.mahasiswaapi.ApplicationKt")
}

dependencies {
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.netty)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.serialization.gson)
  implementation(libs.sqlite.jdbc)
  implementation(libs.logback.classic)
}
