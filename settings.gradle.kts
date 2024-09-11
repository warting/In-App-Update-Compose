
plugins {
    id("com.gradle.enterprise") version "3.18.1"
}

buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
    remote<HttpBuildCache> {
        isEnabled = false
    }
}
// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    pluginManagement {
        repositories {
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }

    repositories {
        google()
        mavenCentral()


    }
    versionCatalogs {
        create("libs") {
            // Noop
        }
    }
}

rootProject.name = "In-app Update Compose"
include(":app")
include(":in-app-update-compose")
include(":in-app-update-compose-mui")