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

@Composable
public fun rememberInAppUpdateState(
    settings: InAppUpdateSettings? = null,
    appUpdateManager: AppUpdateManager? = null,
    highPrioritizeUpdates: Int = 4,
    mediumPrioritizeUpdates: Int = 2,
    promptIntervalHighPrioritizeUpdateInDays: Int = 1,
    promptIntervalMediumPrioritizeUpdateInDays: Int = 1,
    promptIntervalLowPrioritizeUpdateInDays: Int = 7,
    autoTriggerUpdates: Boolean = false,
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
        autoTriggerUpdates = autoTriggerUpdates,
    )
}
