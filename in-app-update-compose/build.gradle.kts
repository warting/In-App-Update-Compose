import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.8.20"
    id("com.vanniktech.maven.publish") version "0.27.0"
}




mavenPublishing {

    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        name.set("In App Update Compose")
        description.set("In-App update compose")
        inceptionYear.set("2021")
        url.set("https://github.com/warting/In-App-Update-Compose/")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("warting")
                name.set("Stefan Wärting")
                url.set("https://github.com/warting/")
            }
        }
        scm {
            url.set("https://github.com/warting/In-App-Update-Compose/")
            connection.set("scm:git:git://github.com/warting/In-App-Update-Compose.git")
            developerConnection.set("scm:git:ssh://git@github.com/warting/In-App-Update-Compose.git")
        }
    }
}

val PUBLISH_GROUP_ID: String by extra(rootProject.group as String)
val PUBLISH_VERSION: String by extra(rootProject.version as String)
val PUBLISH_ARTIFACT_ID by extra("flow")

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION


android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = listOfNotNull(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xskip-prerelease-check"
        )
    }

    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = true
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = true
        disable.add("LintBaseline")
        disable.add("GradleDependency")
        disable.add("NewerVersionAvailable")
        checkDependencies = true
        checkGeneratedSources = false
        sarifOutput = file("../lint-results-inappupdatecompose.sarif")
    }
    namespace = "se.warting.inappupdate"
}

kotlin {
    // https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors
    explicitApi()
}


dependencies {

    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    api("com.google.android.play:app-update-ktx:2.1.0")

    val coroutineVersion = "1.7.1"

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")

    implementation(platform("com.google.android.play:core:1.10.3"))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}