package se.warting.inappupdate.compose

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
public fun NoUi(
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
