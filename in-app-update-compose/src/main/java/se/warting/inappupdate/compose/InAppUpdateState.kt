package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant


private class InMemoryInAppUpdateSettings : InAppUpdateSettings {
    private val _updateDeclined: MutableStateFlow<Declined> =
        MutableStateFlow(Declined(0, Instant.DISTANT_PAST))


    override val updateDeclined: StateFlow<Declined> = _updateDeclined.asStateFlow()
    override fun decline(declined: Declined) {
        _updateDeclined.update {
            declined
        }
    }
}

/**
 * Creates and remembers an InAppUpdateState to manage in-app update behavior in a Compose UI.
 *
 * This is the core function that powers the update checking and state management of the library.
 * It creates a state object that tracks the current update status of the app and provides
 * appropriate actions based on the update availability and configuration.
 *
 * Use this function when you need direct access to the update state to create a custom UI or
 * when you want to implement your own update flow logic. For pre-built UI solutions, consider
 * using [MaterialRequireLatestVersion] or [SilentUpdateHandler] instead.
 *
 * @param settings Optional custom InAppUpdateSettings for persisting update decisions
 * @param appUpdateManager Optional custom AppUpdateManager instance for testing
 * @param highPrioritizeUpdates Number of updates to consider high priority (default: 4)
 * @param mediumPrioritizeUpdates Number of updates to consider medium priority (default: 2)
 * @param promptIntervalHighPrioritizeUpdateInDays Days between prompts for high priority updates (default: 1)
 * @param promptIntervalMediumPrioritizeUpdateInDays Days between prompts for medium priority updates (default: 1)
 * @param promptIntervalLowPrioritizeUpdateInDays Days between prompts for low priority updates (default: 7)
 * @param autoTriggerRequiredUpdates Whether to automatically trigger required updates without user interaction (default: false)
 * @param autoTriggerOptionalUpdates Whether to automatically trigger optional updates without user interaction (default: false)
 * @return An [InAppUpdateState] that represents the current state of available updates
 */
@Composable
public fun rememberInAppUpdateState(
    settings: InAppUpdateSettings? = null,
    appUpdateManager: AppUpdateManager? = null,
    highPrioritizeUpdates: Int = 4,
    mediumPrioritizeUpdates: Int = 2,
    promptIntervalHighPrioritizeUpdateInDays: Int = 1,
    promptIntervalMediumPrioritizeUpdateInDays: Int = 1,
    promptIntervalLowPrioritizeUpdateInDays: Int = 7,
    autoTriggerRequiredUpdates: Boolean = false,
    autoTriggerOptionalUpdates: Boolean = false,
): InAppUpdateState {
    val context = LocalContext.current

    val rememberedAppUpdateManager: AppUpdateManager =
        appUpdateManager ?: remember { AppUpdateManagerFactory.create(context) }

    val rememberedSettings: InAppUpdateSettings =
        settings ?: remember { InMemoryInAppUpdateSettings() }

    return rememberMutableInAppUpdateState(
        appUpdateManager = rememberedAppUpdateManager,
        settings = rememberedSettings,
        numberSettings = NumberSettings(
            highPrioritizeUpdates = highPrioritizeUpdates,
            mediumPrioritizeUpdates = mediumPrioritizeUpdates,
            promptIntervalHighPrioritizeUpdateInDays = promptIntervalHighPrioritizeUpdateInDays,
            promptIntervalMediumPrioritizeUpdateInDays = promptIntervalMediumPrioritizeUpdateInDays,
            promptIntervalLowPrioritizeUpdateInDays = promptIntervalLowPrioritizeUpdateInDays,
        ),
        autoTriggerRequiredUpdates = autoTriggerRequiredUpdates,
        autoTriggerOptionalUpdates = autoTriggerOptionalUpdates,
    )
}
