package tk.zwander.fabricateoverlaysample.ui.pages

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
//        Text(
//            text = stringResource(id = R.string.overlays),
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(8.dp)
//        )
//
//        Spacer(Modifier.size(8.dp))

        RegisteredOverlayList(
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.size(8.dp))

        TextButton(
            onClick = {
                navController.navigate("app_list")
            },
            modifier = Modifier.fillMaxWidth()
                .height(48.dp),
        ) {
            Text(stringResource(R.string.add))
        }
    }
}