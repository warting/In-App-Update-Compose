package se.warting.inappupdate.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.InAppUpdateSettings
import se.warting.inappupdate.compose.InAppUpdateState
import se.warting.inappupdate.compose.rememberInAppUpdateState

@Suppress("LongMethod")
@Composable
public fun MaterialRequireLatestVersion(
    settings: InAppUpdateSettings? = null,
    appUpdateManager: AppUpdateManager? = null,
    highPrioritizeUpdates: Int = 4,
    mediumPrioritizeUpdates: Int = 2,
    promptIntervalHighPrioritizeUpdateInDays: Int = 1,
    promptIntervalMediumPrioritizeUpdateInDays: Int = 1,
    promptIntervalLowPrioritizeUpdateInDays: Int = 7,
    content: @Composable () -> Unit
) {
    val inAppUpdateState: InAppUpdateState = rememberInAppUpdateState(
        settings = settings,
        appUpdateManager = appUpdateManager,
        highPrioritizeUpdates = highPrioritizeUpdates,
        mediumPrioritizeUpdates = mediumPrioritizeUpdates,
        promptIntervalHighPrioritizeUpdateInDays = promptIntervalHighPrioritizeUpdateInDays,
        promptIntervalMediumPrioritizeUpdateInDays = promptIntervalMediumPrioritizeUpdateInDays,
        promptIntervalLowPrioritizeUpdateInDays = promptIntervalLowPrioritizeUpdateInDays,
        autoTriggerRequiredUpdates = true,
        autoTriggerOptionalUpdates = true,
    )
    val scope = rememberCoroutineScope()

    when (inAppUpdateState) {
        is InAppUpdateState.DownloadedUpdate -> {
            if (inAppUpdateState.isRequiredUpdate) {
                LaunchedEffect(inAppUpdateState) {
                    inAppUpdateState.downloadResult.completeUpdate()
                }
                RequiredUpdateDownloadedView {
                    scope.launch {
                        inAppUpdateState.downloadResult.completeUpdate()
                    }
                }
            } else {
                OptionalUpdateDownloadedView {
                    scope.launch {
                        inAppUpdateState.downloadResult.completeUpdate()
                    }
                }
                content()
            }
        }

        is InAppUpdateState.InProgressUpdate -> {
            if (inAppUpdateState.isRequiredUpdate) {
                UpdateInProgress(
                    progress = inAppUpdateState.installState.bytesDownloaded
                        .toFloat() / inAppUpdateState.installState.totalBytesToDownload
                        .toFloat()
                )
            } else {
                content()
            }
        }

        InAppUpdateState.Loading -> {
            LoadingView()
        }
        InAppUpdateState.NotAvailable -> {
            content()
        }
        is InAppUpdateState.OptionalUpdate -> {
            content()
        }

        is InAppUpdateState.RequiredUpdate -> {
            if (inAppUpdateState.shouldPrompt) {
                LoadingView()
            } else {
                UpdateRequiredView {
                    inAppUpdateState.onStartUpdate()
                }
            }
        }

        is InAppUpdateState.Error -> {
            UpdateErrorView()
        }
    }
}
