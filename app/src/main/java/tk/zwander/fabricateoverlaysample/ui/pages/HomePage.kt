package tk.zwander.fabricateoverlaysample.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.RegisteredOverlayList

@Composable
fun HomePage(
    navController: NavController
) {
    Column {
        Button(
            onClick = {
                navController.navigate("app_list")
            }
        ) {
            Text(stringResource(R.string.add))
        }

        RegisteredOverlayList()
    }
}