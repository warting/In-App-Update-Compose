[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose/badge.png)](https://maven-badges.herokuapp.com/maven-central/se.warting.in-app-update/in-app-update-compose)

# In-App update compose

A way to make in app updates in compose

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

Add it to your module dependencies:

```
dependencies {
    implementation("se.warting.in-app-update:in-app-update-compose:<latest_version>")
}
```

## How to use

All you need to do is to use `RequireLatestVersion`:
```
@Composable
fun MainView() {
    RequireLatestVersion {
        Welcome()
    }
}
```
For a full implementation
see: [Full sample](app/src/main/java/se/warting/appupdatecompose/UiActivity.kt)

or if you want more granular control:
```
@Composable
fun InAppUpdate() {
    val updateState = rememberInAppUpdateState()
    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> NotAvailable()
        is AppUpdateResult.Available -> Available(result)
        is AppUpdateResult.InProgress -> InProgress(result)
        is AppUpdateResult.Downloaded -> Downloaded(result)
    }
}
```

For a full implementation
see: [Full sample](app/src/main/java/se/warting/appupdatecompose/MainActivity.kt)
