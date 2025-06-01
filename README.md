[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose/badge.png)](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose)
[![Crowdin](https://badges.crowdin.net/in-app-update-compose/localized.svg)](https://crowdin.com/project/in-app-update-compose)

# In-App update compose

A Jetpack Compose implementation for in-app updates in Android applications.

## How to include in your project

The library is available via MavenCentral:

```
allprojects {
    repositories {
        // ...
        mavenCentral()
    }
}
```


<details>
<summary>Snapshots of the development version are available in Sonatype's snapshots repository.</summary>
<p>

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/se.warting.in-app-update/in-app-update-compose?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/se/warting/in-app-update/in-app-update-compose/)

```groovy
allprojects {
    repositories {
        // ...
        maven {
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }
}
```

</p>
</details>

## Modules

This library consists of two main modules:

### Core Module
Basic functionality without any default UI:

```
dependencies {
    implementation("se.warting.in-app-update:in-app-update-compose:<latest_version>")
}
```

### Material UI Module
Implementation with Material Design components:

```
dependencies {
    implementation("se.warting.in-app-update:in-app-update-compose-mui:<latest_version>")
}
```

## How to use

### Using Material UI Implementation

The simplest way is to use the Material UI implementation with `MaterialRequireLatestVersion`. This component provides a complete Material Design UI for handling all update states:

```kotlin
@Composable
fun MainView() {
    MaterialRequireLatestVersion {
        // Your app content goes here
        Welcome()
    }
}
```

When using `MaterialRequireLatestVersion`:
- A loading indicator appears when checking for updates
- Required updates show a mandatory update screen until the user updates
- Download progress is displayed for required updates being downloaded
- Downloaded required updates show an install prompt
- Optional updates allow continued app usage with normal content
- Update errors show a dedicated error screen

For a full implementation example, see: [Material UI sample](app/src/main/java/se/warting/appupdatecompose/UiActivity.kt)

### Using Core Implementation with Custom UI

For more granular control, you can use the core module and create your own UI:

```kotlin
@Composable
fun InAppUpdate() {
    val updateState = rememberInAppUpdateState()
    when (updateState) {
        InAppUpdateState.Loading -> Loading()
        InAppUpdateState.NotAvailable -> NotAvailable()
        is InAppUpdateState.RequiredUpdate -> RequiredUpdate(updateState)
        is InAppUpdateState.OptionalUpdate -> OptionalUpdate(updateState)
        is InAppUpdateState.InProgressUpdate -> InProgress(updateState)
        is InAppUpdateState.DownloadedUpdate -> Downloaded(updateState)
        is InAppUpdateState.Error -> Error(updateState)
    }
}
```

For a more complex implementation with different update modes, see: [Custom Implementation](app/src/main/java/se/warting/appupdatecompose/MainActivity.kt)

### Additional Options

The library supports various configurations:

```kotlin
MaterialRequireLatestVersion(
    // Custom settings
    highPrioritizeUpdates = 4,
    mediumPrioritizeUpdates = 2,
    promptIntervalHighPrioritizeUpdateInDays = 1,
    promptIntervalMediumPrioritizeUpdateInDays = 1,
    promptIntervalLowPrioritizeUpdateInDays = 7,
) {
    // Your app content
}
```

### Silent Update Handling

For applications that want updates to happen silently in the background with minimal UI intervention, use the `SilentUpdateHandler`:

```kotlin
SilentUpdateHandler(
    errorContent = {
        // Your custom error UI
    },
    content = {
        // Your normal app content
    }
)
```

When using `SilentUpdateHandler`:
- Updates are checked for and processed automatically in the background
- Your regular content is shown during normal operation and for optional updates
- Required updates that are declined will cause the app to close
- No UI is shown during update loading or download processes
- Your error content is only shown if update checking fails

> **Note:** The previous `NoUi` component is deprecated and has been replaced by `SilentUpdateHandler`

