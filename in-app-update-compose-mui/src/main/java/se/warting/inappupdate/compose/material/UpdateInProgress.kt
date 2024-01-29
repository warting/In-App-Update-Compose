package se.warting.inappupdate.compose.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun UpdateInProgress(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.in_app_update_compose_update_progress_title),
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.W700)
        )

        Text(
            text = stringResource(id = R.string.in_app_update_compose_update_progress_body),
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W700)
        )

        if (!progress.isNaN()) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(progress = progress)
        }
    }
}
