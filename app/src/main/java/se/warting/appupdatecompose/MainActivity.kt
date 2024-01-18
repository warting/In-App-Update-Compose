package se.warting.appupdatecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.rememberInAppUpdateState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    InAppUpdate()
                }
            }
        }
    }
}

@Composable
fun InAppUpdate() {
    val updateState = rememberInAppUpdateState()
    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> NotAvailable()
        is AppUpdateResult.Available -> Available(result)
        is AppUpdateResult.InProgress -> InProgress(result)
        is AppUpdateResult.Downloaded -> Downloaded(result)
    }
}

@Composable
fun NotAvailable() {
    Text(text = "NotAvailable")
}

@Composable
fun Available(appUpdateResult: AppUpdateResult.Available) {

    Text(
        text = "App update available.\n" +
                "Versioncode: " + appUpdateResult.updateInfo.availableVersionCode() +
                "\nSince since: " + appUpdateResult.updateInfo.clientVersionStalenessDays +
                " days"
    )
}

@Composable
fun InProgress(appUpdateResult: AppUpdateResult.InProgress) {
    Text(text = "InProgress, downloaded: " + appUpdateResult.installState.bytesDownloaded())
}

@Composable
fun Downloaded(appUpdateResult: AppUpdateResult.Downloaded) {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            appUpdateResult.completeUpdate()
        }
    }) {
        Text(text = "Install")
    }
}
