package se.warting.inappupdate.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.InAppUpdateSettings
import se.warting.inappupdate.compose.InAppUpdateState
import se.warting.inappupdate.compose.rememberInAppUpdateState

/**
 * A Material Design implementation for in-app updates that displays appropriate UI for each update state.
 *
 * This component provides a complete solution for handling in-app updates with Material Design UI:
 * - Shows a loading indicator when checking for updates
 * - Displays a required update screen when updates are mandatory
 * - Shows update download progress for required updates
 * - Presents an install prompt when required updates are downloaded
 * - Allows normal app operation during optional updates
 * - Shows error UI when update checking fails
 *
 * When a required update is detected, the component automatically shows appropriate UI and
 * prevents users from accessing the app until they update. For optional updates, the normal
 * app content continues to be displayed, giving users the choice to update later.
 *
 * @param settings Optional custom settings for update behavior
 * @param appUpdateManager Optional custom AppUpdateManager instance
 * @param highPrioritizeUpdates Number of updates to consider high priority (default: 4)
 * @param mediumPrioritizeUpdates Number of updates to consider medium priority (default: 2)
 * @param promptIntervalHighPrioritizeUpdateInDays Days between prompts for high priority updates (default: 1)
 * @param promptIntervalMediumPrioritizeUpdateInDays Days between prompts for medium priority updates (default: 1)
 * @param promptIntervalLowPrioritizeUpdateInDays Days between prompts for low priority updates (default: 7)
 * @param content The main app content to display when no required updates are needed
 */
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
                    inAppUpdateState.appUpdateResult.completeUpdate()
                }
                RequiredUpdateDownloadedView {
                    scope.launch {
                        inAppUpdateState.appUpdateResult.completeUpdate()
                    }
                }
            } else {
                OptionalUpdateDownloadedView {
                    scope.launch {
                        inAppUpdateState.appUpdateResult.completeUpdate()
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
