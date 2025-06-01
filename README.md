[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose/badge.png)](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose)
[![Crowdin](https://badges.crowdin.net/in-app-update-compose/localized.svg)](https://crowdin.com/project/in-app-update-compose)
![Platform](https://img.shields.io/badge/platform-android-green.svg)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

# In-App Update Compose

A Jetpack Compose implementation for managing in-app updates in Android applications. This library makes it easy to integrate Google Play's in-app update API with your Compose UI, providing both ready-to-use UI components and flexible state management.

## Features

- 🔄 Automatic update checking with Google Play
- 🎨 Material Design UI components for update flows
- 🔧 Configurable update priorities and prompt intervals
- 🔕 Silent update option for minimal UI intervention 
- 🎛️ Custom UI state management support
- 👥 Multiple implementation approaches to fit your needs

## Requirements

- Android API level 21+ (Android 5.0 Lollipop and higher)
- Jetpack Compose
- App published on Google Play Store

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

## Libraries

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

### Update State Flow Diagram

The library manages updates through different states:

```
Loading ──► NotAvailable ────────────────────┐
  │               │                          │
  │               │                          ▼
  └─► RequiredUpdate ◄─┐                   Content
  │     │               │                    ▲
  │     ▼               │                    │
  └─► InProgressUpdate ─┘                    │
  │     │                                    │
  │     ▼                                    │
  └─► DownloadedUpdate ───────────────────►Error
        │                                    ▲
        │                                    │
        └───► CompleteUpdate ────────────────┘
```

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

### Core Update State Management

The `rememberInAppUpdateState` function is the core of this library's update functionality. It handles checking for updates, determining their priority, and managing the update flow:

```kotlin
@Composable
fun YourCustomImplementation() {
    val updateState = rememberInAppUpdateState(
        highPrioritizeUpdates = 4,
        mediumPrioritizeUpdates = 2,
        promptIntervalHighPrioritizeUpdateInDays = 1,
        autoTriggerRequiredUpdates = true
    )
    
    // Use updateState to build your own UI and behavior
}
```

Key features of `rememberInAppUpdateState`:
- Returns an `InAppUpdateState` that represents the current update status
- Automatically checks for available updates from Google Play
- Categorizes updates by priority (high, medium, low)
- Can be configured to automatically trigger required or optional updates
- Controls how frequently users are prompted about updates
- Provides actions for starting, completing, or declining updates

Both `MaterialRequireLatestVersion` and `SilentUpdateHandler` use this function internally, but you can also use it directly when you need complete control over your update UI and behavior.

### Update Types and Priority

The library categorizes updates into different priority levels:

1. **High Priority Updates** - Critical updates like security patches
2. **Medium Priority Updates** - Important feature updates
3. **Low Priority Updates** - Minor enhancements and improvements

You can configure how these updates are handled:

```kotlin
rememberInAppUpdateState(
    highPrioritizeUpdates = 4,           // How many updates to consider high priority
    mediumPrioritizeUpdates = 2,         // How many updates to consider medium priority
    promptIntervalHighPrioritizeUpdateInDays = 1,    // How often to prompt for high priority
    promptIntervalMediumPrioritizeUpdateInDays = 3,  // How often to prompt for medium priority
    promptIntervalLowPrioritizeUpdateInDays = 7      // How often to prompt for low priority
)
```

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

## License

This project is licensed under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
