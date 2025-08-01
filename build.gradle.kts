import io.gitlab.arturbosch.detekt.Detekt
import java.io.FileInputStream
import java.util.Properties

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
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
    alias(libs.plugins.org.jetbrains.kotlinx.binary.compatibility.validator)
    alias(libs.plugins.com.gladed.androidgitversion)
    alias(libs.plugins.compose.compiler) apply false
}

androidGitVersion {
    tagPattern = "^v[0-9]+.*"
}

val localPropertiesFile: File = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val gitOrLocalVersion: String =
    localProperties.getProperty("VERSION_NAME") ?: androidGitVersion.name().replace("v", "")

version = gitOrLocalVersion
group = "se.warting.in-app-update"


apiValidation {
    ignoredProjects.add("app")
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }

    detekt {
        autoCorrect = true
    }

}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    config.from("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
}

versionCatalogUpdate {
    sortByKey.set(true)
    keep {
        keepUnusedVersions.set(false)
    }
}