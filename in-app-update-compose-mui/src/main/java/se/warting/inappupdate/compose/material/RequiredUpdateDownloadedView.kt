package se.warting.inappupdate.compose.material

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun UpdateDownloadedViewPreview() {
    RequiredUpdateDownloadedView(update = {})
}

@Composable
internal fun RequiredUpdateDownloadedView(update: () -> Unit) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.in_app_update_compose_update_complete_title),
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.W700)
            )

            Text(
                text = stringResource(id = R.string.in_app_update_compose_update_complete_body),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W700)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(modifier = Modifier,
                onClick = { update() }) {
                Text(
                    text = stringResource(id = R.string.in_app_update_compose_update_complete_button),
                )
            }
        }
    }
}
