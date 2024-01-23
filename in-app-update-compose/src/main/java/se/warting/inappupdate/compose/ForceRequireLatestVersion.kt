package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable

@Composable
public fun ForceRequireLatestVersion(
    downloadedUpdate: @Composable (e: InAppUpdateState.DownloadedUpdate) -> Unit = {},
    inProgressUpdate: @Composable (e: InAppUpdateState.InProgressUpdate) -> Unit = {},
    loading: @Composable () -> Unit = {},
    optionalUpdate: @Composable (e: InAppUpdateState.OptionalUpdate) -> Unit =
        { e -> e.onStartUpdate(Mode.IMMEDIATE) },
    requiredUpdate: @Composable (e: InAppUpdateState.RequiredUpdate) -> Unit =
        { e -> e.onStartUpdate() },
    error: (@Composable (e: InAppUpdateState.Error) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when (val inAppUpdateState: InAppUpdateState = rememberInAppUpdateState()) {
        is InAppUpdateState.DownloadedUpdate -> downloadedUpdate(inAppUpdateState)
        is InAppUpdateState.InProgressUpdate -> inProgressUpdate(inAppUpdateState)
        InAppUpdateState.Loading -> loading()
        is InAppUpdateState.OptionalUpdate -> optionalUpdate(inAppUpdateState)
        is InAppUpdateState.RequiredUpdate -> requiredUpdate(inAppUpdateState)
        InAppUpdateState.NotAvailable -> content()
        is InAppUpdateState.Error -> {
            if (error != null) {
                error(inAppUpdateState)
            } else
                content()
        }
    }
}
