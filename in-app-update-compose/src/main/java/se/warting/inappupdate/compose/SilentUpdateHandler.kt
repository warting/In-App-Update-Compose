package se.warting.inappupdate.compose

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch

/**
 * A minimal implementation for handling in-app updates with virtually no UI.
 *
 * This component is designed for applications that want to:
 * - Handle updates silently in the background
 * - Show their own content during normal operation
 * - Only interrupt the user experience when absolutely necessary
 *
 * When using NoUi:
 * - Your app will automatically check for and process updates in the background
 * - For optional updates, your app content will continue to be displayed
 * - For required/mandatory updates, the app will close if the user declines the update
 * - No UI will be shown during update loading or download processes
 * - Your provided error content will only be shown if update checking fails
 *
 * @param errorContent Composable to display when there's an update error
 * @param content Main content to display during normal app operation
 */
@Composable
public fun SilentUpdateHandler(
    errorContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    when (val inAppUpdateState: InAppUpdateState = rememberInAppUpdateState(
        autoTriggerRequiredUpdates = true,
        autoTriggerOptionalUpdates = true,
    )) {
        is InAppUpdateState.DownloadedUpdate -> {
            // CircularProgressIndicator()
            LaunchedEffect(inAppUpdateState) {
                launch {
                    inAppUpdateState.onCompleteUpdate(inAppUpdateState.appUpdateResult)
                }
            }
        }

        is InAppUpdateState.InProgressUpdate -> {
            // CircularProgressIndicator()
        }

        InAppUpdateState.Loading -> {
            // CircularProgressIndicator()
        }

        is InAppUpdateState.OptionalUpdate -> {
            content()
        }

        is InAppUpdateState.RequiredUpdate -> {
            if (!inAppUpdateState.shouldPrompt) {
                val activity = LocalActivity.current
                LaunchedEffect(inAppUpdateState) {
                    activity?.finish()
                }
            } else {
                // CircularProgressIndicator()
            }
        }

        InAppUpdateState.NotAvailable -> content()
        is InAppUpdateState.Error -> {
            errorContent()
        }
    }
}

@Deprecated(
    message = "Use SilentUpdateHandler instead for a no-UI update handling experience.",
    replaceWith = ReplaceWith("SilentUpdateHandler(errorContent, content)")
)
@Composable
public fun NoUi(
    errorContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    SilentUpdateHandler(errorContent, content)
}