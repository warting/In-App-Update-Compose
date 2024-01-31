package se.warting.inappupdate.compose

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.requestUpdateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import logcat.LogPriority
import logcat.logcat
import com.google.android.play.core.install.model.ActivityResult as UpdateActivityResult


private const val LOGTAG = "InAppUpdate"

public interface InAppUpdateSettings {

    public val updateDeclined: StateFlow<Declined>
    public fun decline(declined: Declined)
}

internal data class NumberSettings(
    val highPrioritizeUpdates: Int,
    val mediumPrioritizeUpdates: Int,
    val promptIntervalHighPrioritizeUpdateInDays: Int,
    val promptIntervalMediumPrioritizeUpdateInDays: Int,
    val promptIntervalLowPrioritizeUpdateInDays: Int,
)

public data class Declined(val version: Int, val date: Instant)

@Suppress("LongMethod")
@Composable
internal fun rememberMutableInAppUpdateState(
    settings: InAppUpdateSettings,
    appUpdateManager: AppUpdateManager,
    numberSettings: NumberSettings,
    autoTriggerRequiredUpdates: Boolean = false,
    autoTriggerOptionalUpdates: Boolean = false,
): InAppUpdateState {
    var onResultState: (ActivityResult) -> Unit by remember { mutableStateOf({}) }
    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { onResultState(it) }
    )

    var recomposeKey by remember {
        mutableIntStateOf(0)
    }

    val inAppUpdateState: MutableInAppUpdateState = remember {
        MutableInAppUpdateState(
            appUpdateManager = appUpdateManager,
            produceIntentLauncher = { onResult ->
                onResultState = onResult
                intentLauncher
            },
            recompose = {
                recomposeKey++
            },
            settings = settings,
        )
    }

    val state: InAppUpdateState = produceState<InAppUpdateState>(
        initialValue = InAppUpdateState.Loading,
        key1 = recomposeKey
    ) {
        combine(
            appUpdateManager.requestUpdateFlow(),
            settings.updateDeclined
        ) { appUpdateState, declined ->
            inAppUpdateState.fixState(
                appUpdateResult = appUpdateState,
                declined = declined,
                numberSettings = numberSettings,
                inAppUpdateState = inAppUpdateState
            )
        }.catch { exception ->
            emit(InAppUpdateState.Error(exception))
        }.collect { value = it }
    }.value

    LaunchedEffect(state) {
        when (state) {
            is InAppUpdateState.DownloadedUpdate -> {
                // Do nothing
            }

            is InAppUpdateState.Error -> {
                // Do nothing
            }

            is InAppUpdateState.InProgressUpdate -> {
                // Do nothing
            }

            InAppUpdateState.Loading -> {
                // Do nothing
            }

            InAppUpdateState.NotAvailable -> {
                // Do nothing
            }

            is InAppUpdateState.OptionalUpdate -> {
                if (state.shouldPrompt && autoTriggerOptionalUpdates) {
                    state.onStartUpdate(Mode.FLEXIBLE)
                }
            }

            is InAppUpdateState.RequiredUpdate -> {
                if (state.shouldPrompt && autoTriggerRequiredUpdates) {
                    state.onStartUpdate()
                }
            }
        }
    }

    return state
}


public sealed class InAppUpdateState {
    public data object Loading : InAppUpdateState()
    public data object NotAvailable : InAppUpdateState()
    public data class RequiredUpdate(
        val onStartUpdate: () -> Unit,
        val appUpdateInfo: MyAppUpdateInfo,
        val shouldPrompt: Boolean,
    ) : InAppUpdateState()

    public data class OptionalUpdate(
        val onStartUpdate: (mode: Mode) -> Unit,
        val onDeclineUpdate: () -> Unit,
        val shouldPrompt: Boolean,
        val appUpdateInfo: MyAppUpdateInfo,
    ) : InAppUpdateState()

    public data class DownloadedUpdate(
        val appUpdateResult: AppUpdateResult.Downloaded,
        val isRequiredUpdate: Boolean,
        val onCompleteUpdate: suspend (AppUpdateResult.Downloaded) -> Unit
    ) : InAppUpdateState()

    public data class InProgressUpdate(
        val installState: MyInstallState,
        val isRequiredUpdate: Boolean,
    ) : InAppUpdateState()

    public data class Error(val exception: Throwable) : InAppUpdateState()
}

internal class MutableInAppUpdateState(
    private val appUpdateManager: AppUpdateManager,
    private val produceIntentLauncher:
        (onResult: (ActivityResult) -> Unit) -> ActivityResultLauncher<IntentSenderRequest>,
    private val recompose: () -> Unit,
    private val settings: InAppUpdateSettings,
) {

    var updateMode: Mode? = null
    private fun startUpdate(updateInfo: AppUpdateInfo, mode: Mode) {

        updateMode = mode

        val type: AppUpdateOptions = when (mode) {
            Mode.FLEXIBLE -> AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            Mode.IMMEDIATE -> AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        }

        appUpdateManager.startUpdateFlowForResult(
            updateInfo,
            produceIntentLauncher { result ->
                handleUpdateFlowResult(updateInfo, result)
            }.starter(),
            type,
            0
        )
    }


    internal fun fixState(
        appUpdateResult: AppUpdateResult,
        declined: Declined,
        numberSettings: NumberSettings,
        inAppUpdateState: MutableInAppUpdateState
    ): InAppUpdateState {
        return appUpdateResult.toInAppUpdateState(
            numberSettings = numberSettings,
            declined = declined,
            onStartUpdate = { updateInfo, updateType ->
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: Starting update of type " + describeUpdateType(
                        updateType
                    )
                }
                inAppUpdateState.startUpdate(updateInfo, updateType)
            },
            onDeclineUpdate = { updateInfo ->
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: Declined flexible update"
                }
                inAppUpdateState.declineUpdate(updateInfo)
            },
            onCompleteUpdate = { result ->
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: Completing flexible update"
                }
                result.completeUpdate()
            }
        )
    }


    private fun describeUpdateType(updateType: Mode): String = when (updateType) {
        Mode.IMMEDIATE -> "IMMEDIATE"
        Mode.FLEXIBLE -> "FLEXIBLE"
    }

    private fun AppUpdateResult.toInAppUpdateState(
        numberSettings: NumberSettings,
        declined: Declined,
        onStartUpdate: (updateInfo: AppUpdateInfo, updateType: Mode) -> Unit,
        onDeclineUpdate: (AppUpdateInfo) -> Unit,
        onCompleteUpdate: suspend (AppUpdateResult.Downloaded) -> Unit
    ): InAppUpdateState = when (this) {
        AppUpdateResult.NotAvailable -> InAppUpdateState.NotAvailable
        is AppUpdateResult.Available -> if (shouldUpdateImmediately(updateInfo, numberSettings)) {
            InAppUpdateState.RequiredUpdate(
                onStartUpdate = { onStartUpdate(updateInfo, Mode.IMMEDIATE) },
                appUpdateInfo = updateInfo.toAppUpdateInfo(),
                shouldPrompt = shouldPromptToRequiredUpdate(declined, updateInfo, numberSettings),
            )
        } else {
            InAppUpdateState.OptionalUpdate(
                onStartUpdate = { mode -> onStartUpdate(updateInfo, mode) },
                onDeclineUpdate = { onDeclineUpdate(updateInfo) },
                shouldPrompt = shouldPromptToUpdate(declined, updateInfo, numberSettings),
                appUpdateInfo = updateInfo.toAppUpdateInfo()

            )
        }

        is AppUpdateResult.Downloaded -> {
            InAppUpdateState.DownloadedUpdate(
                appUpdateResult = this,
                isRequiredUpdate = updateMode == Mode.IMMEDIATE,
                onCompleteUpdate = onCompleteUpdate
            )
        }

        is AppUpdateResult.InProgress -> {
            InAppUpdateState.InProgressUpdate(
                installState = installState.toAppInstallState(),
                isRequiredUpdate = updateMode == Mode.IMMEDIATE,
            )
        }
    }

    private fun InstallState.toAppInstallState(): MyInstallState =
        MyInstallState(
            bytesDownloaded = bytesDownloaded(),
            totalBytesToDownload = totalBytesToDownload(),
            installErrorCode = installErrorCode(),
            packageName = packageName(),
            installStatus = installStatus(),
        )


    private fun handleUpdateFlowResult(updateInfo: AppUpdateInfo, updateResult: ActivityResult) {
        when (updateResult.resultCode) {
            Activity.RESULT_OK -> {
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: result OK"
                }
            }

            Activity.RESULT_CANCELED -> {
                //log.info("UpdaterState: result CANCELED", emptyBreadcrumb)
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: result CANCELED"
                }
                declineUpdate(updateInfo)
            }

            UpdateActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                logcat(
                    priority = LogPriority.INFO,
                    tag = LOGTAG,
                ) {
                    "UpdaterState: result FAILED"
                }
            }
        }
        // Changing the key restarts the flow in `update`, creating a new subscription to
        // AppUpdateManager.requestUpdateFlow() and causing readers of that state to recompose.
        recompose()
    }


    internal fun declineUpdate(updateInfo: AppUpdateInfo) {
        // Don't store declined state for high-priority updates.
        // if (updateInfo.updatePriority() >= numberSettings.highPrioritizeUpdates) return
        settings.decline(Declined(updateInfo.availableVersionCode(), Clock.System.now()))
    }
}


internal fun ActivityResultLauncher<IntentSenderRequest>.starter(): IntentSenderForResultStarter =
    IntentSenderForResultStarter { intentSender, _, fillInIntent, flagsMask, flagsValue, _, _ ->
        launch(
            IntentSenderRequest.Builder(intentSender)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValue, flagsMask)
                .build()
        )
    }

public data class MyAppUpdateInfo(
    val versionCode: Int,
    val staleDays: Int?,
    val priority: Int,
)

internal fun AppUpdateInfo.toAppUpdateInfo(): MyAppUpdateInfo = MyAppUpdateInfo(
    versionCode = availableVersionCode(),
    staleDays = clientVersionStalenessDays(),
    priority = updatePriority(),
)


public data class MyInstallState(
    val bytesDownloaded: Long,
    val installErrorCode: Int,
    val packageName: String,
    val totalBytesToDownload: Long,
    val installStatus: Int,
)


internal fun shouldUpdateImmediately(
    updateInfo: AppUpdateInfo,
    numberSettings: NumberSettings
): Boolean = with(updateInfo) {
    updateInfo.updateAvailability() ==
            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ||
            (isImmediateUpdateAllowed && updatePriority() >= numberSettings.highPrioritizeUpdates)
}


@Suppress("ReturnCount")
internal fun shouldPromptToUpdate(
    declined: Declined,
    updateInfo: AppUpdateInfo,
    numberSettings: NumberSettings,
): Boolean {
    with(updateInfo) {
        // No point in prompting if we're not allowed to update.
        if (!isFlexibleUpdateAllowed) return false

        val promptIntervalDays = when {
            // For medium-priority updates, prompt once per day.
            updatePriority() >= numberSettings.mediumPrioritizeUpdates ->
                numberSettings.promptIntervalMediumPrioritizeUpdateInDays
            // For low-priority updates, prompt once per week.
            else -> numberSettings.promptIntervalLowPrioritizeUpdateInDays
        }

        // Always prompt if the user has never declined this update.
        if (declined.version != availableVersionCode()) {
            return true
        }

        // To prompt for an optional update, the update must be at least
        // `promptIntervalDays` old and the user declined this update at
        // least `promptIntervalDays` ago (or has never declined).
        if ((clientVersionStalenessDays()
                ?: numberSettings.promptIntervalLowPrioritizeUpdateInDays) < promptIntervalDays
        ) return false

        val daysSinceLastDecline = declined.date.daysUntil(
            Clock.System.now(),
            TimeZone.currentSystemDefault()
        )

        if (daysSinceLastDecline < promptIntervalDays) {
            return false
        }

        return true
    }
}

@Suppress("ReturnCount")
internal fun shouldPromptToRequiredUpdate(
    declined: Declined,
    updateInfo: AppUpdateInfo,
    numberSettings: NumberSettings,
): Boolean {
    with(updateInfo) {
        // No point in prompting if we're not allowed to update.
        if (!isImmediateUpdateAllowed) return false

        val promptIntervalDays = numberSettings.promptIntervalHighPrioritizeUpdateInDays

        // Always prompt if the user has never declined this update.
        if (declined.version != availableVersionCode()) {
            return true
        }

        // To prompt for an optional update, the update must be at least
        // `promptIntervalDays` old and the user declined this update at
        // least `promptIntervalDays` ago (or has never declined).
        if ((clientVersionStalenessDays()
                ?: numberSettings.promptIntervalLowPrioritizeUpdateInDays) < promptIntervalDays
        ) return false

        val daysSinceLastDecline = declined.date.daysUntil(
            Clock.System.now(),
            TimeZone.currentSystemDefault()
        )

        if (daysSinceLastDecline < promptIntervalDays) {
            return false
        }

        return true
    }
}