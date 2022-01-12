package views

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import components.ToolBar
import service.model.SwitchRoutes
import themes.LightThemeColors
import java.awt.Dimension

@ExperimentalFoundationApi
@Composable
fun AppLayout(args: Array<String>) {
    val currentWindow = LocalAppWindow.current
    val setFullScreen = remember { mutableStateOf(false) }
    val showLive: MutableState<Boolean> = remember { mutableStateOf(false) }
    val switchRoutes = remember { mutableStateOf(SwitchRoutes(0, mutableListOf())) }

    if (setFullScreen.value) {
        currentWindow.makeFullscreen()
    } else {
        currentWindow.restore()
    }
    currentWindow.keyboard.setShortcut(Key.Spacebar) {
        println("space bar pressed")
    }

    currentWindow.window.minimumSize = Dimension(600, 480)

    val scanning = remember { mutableStateOf(false) }
    val modifier = Modifier
    modifier.background(Color.Black)

    MaterialTheme(colors = LightThemeColors) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            if (!setFullScreen.value) {
                ToolBar(switchRoutes = switchRoutes, scanning = scanning, showLive = showLive)
            }
            PlayerControl(
                setFullScreen = setFullScreen,
                scanning = scanning,
                showLive = showLive,
                switchRoutes = switchRoutes
            )
        }
    }
}
