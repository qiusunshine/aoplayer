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
import uk.co.caprica.vlcj.log.LogLevel
import utils.Timber

@ExperimentalFoundationApi
@Composable
fun PlayerFrame(
    modifier: Modifier,
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>, //this works just fine
) {
    SideEffect {
        val log = mediaPlayerComponent.value.mediaPlayerFactory().application().newLog()
        log.addLogListener { level, module, file, line, name, header, id, message ->
            val msg = "TAG: ${level?.name}, message: $message"
            if (level == LogLevel.ERROR) {
                Timber.d(msg)
            } else {
                println(msg)
            }
        }
    }
    SwingPanel(
        background = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp), //I have no idea why I need to set padding for the component to autoresize
        factory = {
            mediaPlayerComponent.value
        }
    )
}


