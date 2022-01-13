package views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import utils.Timber
import utils.ViewUtils

@Composable
fun LogDialog(
    show: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = {
            show.value = false
        }, properties = DialogProperties(
            title = "查看日志",
            icon = ViewUtils.getIcon(),
            size = IntSize(800, 600)
        ),
    ) {
        Surface(
            color = Color.DarkGray
        ) {
            TextField(
                value = Timber.logs.joinToString("\n"),
                modifier = Modifier.fillMaxSize(),
                onValueChange = {

                }
            )
        }
    }
}