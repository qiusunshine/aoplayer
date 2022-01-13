package views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import components.ToolBar
import service.model.SwitchRoutes
import themes.LightThemeColors
import java.awt.Dimension

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun AppLayout(args: Array<String>) {
    val currentWindow = LocalAppWindow.current
    val setFullScreen = remember { mutableStateOf(false) }
    val showLive: MutableState<Boolean> = remember { mutableStateOf(false) }
    val switchRoutes = remember { mutableStateOf(SwitchRoutes(0, mutableListOf())) }
    val scanning = remember { mutableStateOf(false) }

    LaunchedEffect(setFullScreen.value) {
        if (setFullScreen.value) {
            currentWindow.makeFullscreen()
        } else {
            currentWindow.restore()
        }
    }

    LaunchedEffect(Unit) {
        currentWindow.keyboard.setShortcut(Key.Spacebar) {
            println("space bar pressed")
        }
        currentWindow.window.minimumSize = Dimension(600, 480)
    }

    MaterialTheme(colors = LightThemeColors) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(visible = !setFullScreen.value, modifier = Modifier.background(Color.DarkGray)) {
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
