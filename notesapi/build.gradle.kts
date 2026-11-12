plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  application
}

kotlin {
  jvmToolchain(21)
}

application {
  mainClass.set("com.example.notesapi.ApplicationKt")
}

dependencies {
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.netty)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.server.status.pages)
  implementation(libs.ktor.server.call.logging)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.logback.classic)
  implementation(libs.exposed.core)
  implementation(libs.exposed.jdbc)
  implementation(libs.sqlite.jdbc)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.junit)
}
