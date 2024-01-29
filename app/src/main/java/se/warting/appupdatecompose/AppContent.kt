package se.warting.appupdatecompose

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@Composable
fun AppContent(content: @Composable () -> Unit) {
    Scaffold(topBar = {
        TopAppBar {
            Text("App update compose")
        }
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            val context = LocalContext.current
            Text(stringResource(R.string.package_name, context.packageName))
            Text(
                stringResource(
                    R.string.version_name, context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).versionName
                )
            )
            Text(
                stringResource(
                    R.string.version_code, getVersionCode(context).toString()
                )
            )

            content()
        }
    }
}


@Suppress("DEPRECATION")
private fun getVersionCode(context: Context): Long {
    val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0);
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pInfo.longVersionCode
    } else {
        pInfo.versionCode.toLong()
    }
}