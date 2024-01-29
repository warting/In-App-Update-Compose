package se.warting.appupdatecompose

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri

@Composable
fun TogglesPrompt() {
    val context = LocalContext.current
    Column {
        Text(
            stringResource(R.string.toggles_prompt_text)
        )
        Button(onClick = {
            // Check if toggles is installed or launch play store to install it
            // https://play.google.com/store/apps/details?id=se.eelde.toggles
            val intent =
                context.packageManager.getLaunchIntentForPackage("se.eelde.toggles")
            if (intent != null) {
                context.startActivity(intent)
            } else {
                val browseIntent = Intent(Intent.ACTION_VIEW)
                browseIntent.data =
                    "https://play.google.com/store/apps/details?id=se.eelde.toggles".toUri()
                context.startActivity(browseIntent)
            }
        }) {
            Text(text = stringResource(R.string.install_open_toggles_app))
        }
    }
}