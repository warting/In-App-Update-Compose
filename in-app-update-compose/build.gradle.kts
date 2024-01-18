import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.com.vanniktech.maven.publish)
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
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
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

    val composeBom = platform(libs.androidx.compose.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    api(libs.com.google.android.play.app.update.ktx)

    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.play.services)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)

    implementation(platform(libs.com.google.android.play.core))
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.com.google.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)
}