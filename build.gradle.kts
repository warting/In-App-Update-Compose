import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.com.android.tools.build.gradle)
        classpath(libs.org.jetbrains.kotlin.kotlin.gradle.plugin)
        classpath(libs.io.gitlab.arturbosch.detekt.detekt.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
    alias(libs.plugins.org.jetbrains.kotlinx.binary.compatibility.validator)
    alias(libs.plugins.com.gladed.androidgitversion)
}

androidGitVersion {
    tagPattern = "^v[0-9]+.*"
}

val gitOrLocalVersion: String =
    com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
        .getProperty("VERSION_NAME", androidGitVersion.name().replace("v", ""))

version = gitOrLocalVersion
group = "se.warting.in-app-update"


apiValidation {
    ignoredProjects.add("app")
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
    }

    detekt {
        autoCorrect = true
    }

}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    config = files("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")

    reports {
        html.enabled = true
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

versionCatalogUpdate {
    sortByKey.set(true)
    keep {
        keepUnusedVersions.set(false)
        keepUnusedLibraries.set(false)
        keepUnusedPlugins.set(false)
    }
}

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}


task<Delete>("clean") {
    delete(rootProject.buildDir)
}
