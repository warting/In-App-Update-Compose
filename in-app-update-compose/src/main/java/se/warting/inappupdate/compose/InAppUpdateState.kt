package se.warting.inappupdate.compose

import androidx.compose.runtime.Composable
import com.google.android.play.core.ktx.AppUpdateResult

@Composable
public fun rememberInAppUpdateState(): InAppUpdateState {
    return rememberMutableInAppUpdateState()
}

public interface InAppUpdateState {
    public val appUpdateResult: AppUpdateResult
}
