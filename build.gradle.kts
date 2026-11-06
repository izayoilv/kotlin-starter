import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.wrapper.Wrapper

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.kotlin.jvm) apply false
}

// The wrapper task rewrites gradle-wrapper.properties, so keep these here.
tasks.named<Wrapper>("wrapper") {
  distributionType = Wrapper.DistributionType.ALL
  networkTimeout.set(60_000)
  retries.set(3)
  retryBackOffMs.set(1_000)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
  rejectPreReleases = true
}
