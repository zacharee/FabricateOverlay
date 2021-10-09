package tk.zwander.fabricateoverlaysample.ui.pages

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.RegisteredOverlayList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    navController: NavController
) {
    Column {
        (LocalContext.current as Activity).setTitle(R.string.overlays)

        RegisteredOverlayList(
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.size(8.dp))

        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    navController.navigate("app_list")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) {
                Text(
                    text = stringResource(R.string.add_overlay),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}