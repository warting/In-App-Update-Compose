
plugins {
    id("com.gradle.enterprise") version "3.10.3"
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

rootProject.name = "In-app Update Compose"
include(":app")
include(":inappupdatecompose")
