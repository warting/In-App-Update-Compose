package se.warting.inappupdate.compose.material

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Preview
@Composable
private fun UpdateErrorViewPreview() {
    UpdateErrorView()
}

@Composable
internal fun UpdateErrorView() {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.in_app_update_compose_checking_for_updates_failed),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.W700)
            )

            Text(
                text = stringResource(id = R.string.in_app_update_compose_update_from_play_store),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W700)
            )

            Spacer(modifier = Modifier.height(50.dp))

            val context = LocalContext.current
            Button(modifier = Modifier,
                onClick = {
                    val appPackageName = context.applicationContext.packageName
                    @Suppress("SwallowedException")
                    try {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "market://details?id=$appPackageName".toUri()
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                            )
                        )
                    }
                }) {
                Text(
                    text = stringResource(id = R.string.in_app_update_compose_update_play_store),
                )
            }
        }
    }
}
