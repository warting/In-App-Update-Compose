package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable
import com.google.android.play.core.ktx.AppUpdateResult

@Composable
fun rememberInAppUpdateState(): InAppUpdateState {
    return rememberMutableInAppUpdateState()
}

interface InAppUpdateState {
    val appUpdateResult: AppUpdateResult
}
