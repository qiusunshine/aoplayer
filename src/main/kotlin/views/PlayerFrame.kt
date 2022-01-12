package views

import KryerMediaPlayerComponent
import androidx.compose.desktop.SwingPanel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@Composable
fun PlayerFrame(
    modifier: Modifier,
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>, //this works just fine
    readyToPlay: MutableState<Boolean>,
) {
    SideEffect {
        readyToPlay.value = true
        mediaPlayerComponent.value.mediaPlayerFactory().application().newLog()
    }
    SwingPanel(
        background = Color.Green,
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp), //I have no idea why I need to set padding for the component to autoresize
        factory = {
            mediaPlayerComponent.value
        }
    )
}


