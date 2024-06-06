import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.com.vanniktech.maven.publish)
    alias(libs.plugins.compose.compiler)
}

mavenPublishing {

    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        name.set("In App Update Compose - Material UI")
        description.set("In-App update compose with material UI")
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

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION


android {
    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = true
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = true
        disable.add("LintBaseline")
        disable.add("GradleDependency")
        disable.add("NewerVersionAvailable")
        disable.add("AndroidGradlePluginVersion")
        checkDependencies = true
        checkGeneratedSources = false
        sarifOutput = file("lint-results-in-app-update-compose-mui.sarif")
    }
    namespace = "se.warting.inappupdate.compose.material"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
kotlin {
    explicitApi()
    jvmToolchain(17)
}
dependencies {

    val composeBom = platform(libs.androidx.compose.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material)
    api(project(":in-app-update-compose"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
}