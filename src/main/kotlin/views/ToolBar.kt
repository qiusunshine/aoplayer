package components

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import controls.getPlayUrl
import controls.openFile
import controls.setPlayUrl
import service.model.SwitchRoutes

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    switchRoutes: MutableState<SwitchRoutes>,
    scanning: MutableState<Boolean>,
    showLive: MutableState<Boolean>
) {
    val parentWindow = LocalAppWindow.current
    val text = remember { mutableStateOf("") }

    LaunchedEffect(getPlayUrl(switchRoutes)) {
        val now = getPlayUrl(switchRoutes)
        if (now != text.value) {
            text.value = now
        }
    }

    TopAppBar(Modifier.padding(0.dp), backgroundColor = Color.DarkGray) {
        IconButton(onClick = {
            openFile(
                parentWindow = parentWindow,
                ok = {
                    setPlayUrl(switchRoutes, it)
                }
            )
        }) {
            Icon(TablerIcons.FileText, contentDescription = "本地文件")
        }

        IconButton(onClick = {
            showLive.value = !showLive.value
        }) {
            Icon(if (!showLive.value) TablerIcons.DeviceTv else TablerIcons.Video, contentDescription = "直播")
        }

        TextField(
            value = text.value,
            placeholder = @Composable {
                Text(if (scanning.value) "正在扫描局域网...请提前打开网页投屏（可手动输入地址）" else "输入网页投屏地址或者视频地址")
            },
            singleLine = true,
            onValueChange = {
                text.value = it
            },
            modifier = modifier.weight(1f),
            shape = MaterialTheme.shapes.small.copy(ZeroCornerSize),
        )
        IconButton(onClick = {
            setPlayUrl(switchRoutes, text.value)
        }) {
            Icon(TablerIcons.Check, contentDescription = "Settings")
        }
        IconButton(onClick = {
            parentWindow.close()
        }) {
            Icon(TablerIcons.X, contentDescription = "Close")
        }
    }
}