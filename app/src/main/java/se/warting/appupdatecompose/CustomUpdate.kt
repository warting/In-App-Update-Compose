package se.warting.appupdatecompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.InAppUpdateState
import se.warting.inappupdate.compose.Mode
import se.warting.inappupdate.compose.rememberInAppUpdateState

@Suppress("LongMethod")
@Composable
fun CustomUpdate() {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        when (val updateState: InAppUpdateState = rememberInAppUpdateState(
            highPrioritizeUpdates = 0,
            autoTriggerRequiredUpdates = true,
            autoTriggerOptionalUpdates = true,
        )) {
            is InAppUpdateState.DownloadedUpdate -> {
                val scope = rememberCoroutineScope()

                if(updateState.isRequiredUpdate) {
                    // App has downloaded a required update, show a button to install
                    AppContent {
                        Column {
                            Text("Required App update downloaded, ready to install")
                            Button(onClick = {
                                scope.launch {
                                    updateState.downloadResult.completeUpdate()
                                }
                            }) {
                                Text(text = "Install")
                            }
                        }
                    }
                }
                else {
                    // App has downloaded a flexible update, show a button to install
                    AppContent {
                        Column {
                            Text("Optional App update downloaded, ready to install")
                            Button(onClick = {
                                scope.launch {
                                    updateState.downloadResult.completeUpdate()
                                }
                            }) {
                                Text(text = "Install")
                            }
                        }
                    }
                }
            }

            is InAppUpdateState.InProgressUpdate -> {
                if(updateState.isRequiredUpdate) {
                    // App is processing a required update, show a progress indicator
                    AppContent {
                        Column {
                            Text("Processing required update...")
                            LinearProgressIndicator(
                                progress = updateState.installState.bytesDownloaded
                                    .toFloat()
                                        / updateState.installState.totalBytesToDownload
                                    .toFloat()
                            )
                        }
                    }
                }
                else {
                    // App is processing a optional update,
                    // show app content and maybe a progress indicator
                    AppContent {
                        Column {
                            Text("Processing optional update...")
                            LinearProgressIndicator(
                                progress = updateState.installState.bytesDownloaded
                                    .toFloat()
                                        / updateState.installState.totalBytesToDownload
                                    .toFloat()
                            )
                        }
                    }
                }
            }

            InAppUpdateState.Loading -> {
                AppContent {
                    Column {
                        Text("Checking for updates...")
                        CircularProgressIndicator()
                    }
                }
            }

            InAppUpdateState.NotAvailable -> {
                Text("No app updates available. Your app is up to date")
            }

            is InAppUpdateState.OptionalUpdate -> {
                AppContent {
                    OptionalUpdateAvailableView(updateState)
                }
            }

            is InAppUpdateState.RequiredUpdate -> {
                RequiredUpdateAvailable(updateState)
            }

            is InAppUpdateState.Error -> {
                AppContent {
                    ErrorView(updateState)
                }
            }
        }
    }
}

@Composable
fun ErrorView(updateState: InAppUpdateState.Error) {
    SelectionContainer {
        Column {
            Text("An error was encountered")
            Text(text = updateState.exception.message ?: "Unknown error")
        }
    }
}

@Composable
fun NotAvailable() {
    Text(text = "NotAvailable")
}


@Composable
fun OptionalUpdateAvailableDialog(appUpdateResult: InAppUpdateState.OptionalUpdate) {
    AlertDialog(onDismissRequest = { appUpdateResult.onDeclineUpdate() },
        title = { Text(text = "Mr Update") },
        text = {
            Text(
                text = "Optional update available.\n" +
                        "Versioncode: ${appUpdateResult.appUpdateInfo.versionCode}\n" +
                        "Stale ${appUpdateResult.appUpdateInfo.staleDays} days\n" +
                        "Should prompt user: ${appUpdateResult.shouldPrompt}"
            )
        },
        confirmButton = {
            TextButton(onClick = { appUpdateResult.onStartUpdate(Mode.FLEXIBLE) }) {
                Text(text = "Start flexible update")
            }
        },
        dismissButton = {
            TextButton(onClick = { appUpdateResult.onDeclineUpdate() }) {
                Text(text = "Dismiss")
            }
        }
    )
}

@Composable
fun OptionalUpdateAvailableView(appUpdateResult: InAppUpdateState.OptionalUpdate) {
    Column {
        Text(
            text = "Optional update available.\n" +
                    "Versioncode: ${appUpdateResult.appUpdateInfo.versionCode}\n" +
                    "Stale ${appUpdateResult.appUpdateInfo.staleDays} days\n" +
                    "Priority: ${appUpdateResult.appUpdateInfo.priority}\n" +
                    "Should prompt user: ${appUpdateResult.shouldPrompt}"
        )
        Button(onClick = { appUpdateResult.onStartUpdate(Mode.FLEXIBLE) }) {
            Text(text = "Start flexible update")
        }
    }
}

@Composable
fun RequiredUpdateAvailable(appUpdateResult: InAppUpdateState.RequiredUpdate) {
    Column {
        Text(
            text = "Required update available.\n" +
                    "Versioncode: ${appUpdateResult.appUpdateInfo.versionCode}\n" +
                    "Stale ${appUpdateResult.appUpdateInfo.staleDays} days"

        )

        Button(onClick = {
            appUpdateResult.onStartUpdate()
        }) {
            Text(text = "Start Immediate Update")
        }
    }
}

@Composable
fun InProgress(appUpdateResult: AppUpdateResult.InProgress) {
    Text(text = "InProgress, downloaded: " + appUpdateResult.installState.bytesDownloaded())
}

@Composable
fun Downloaded(appUpdateResult: AppUpdateResult.Downloaded) {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            appUpdateResult.completeUpdate()
        }
    }) {
        Text(text = "Install")
    }
}