package components

import KryerMediaPlayerComponent
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import controls.seekBackward
import controls.seekForward
import controls.togglePlay

@Composable
fun ControlButtons(
    playing: MutableState<Boolean>,
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>,
    modifier: Modifier = Modifier,
    setFullScreen: MutableState<Boolean>,
    toggleFullScreen: () -> Unit
) {
    val controlButtons = mapOf<ImageVector, () -> Unit>(
        Pair(TablerIcons.PlayerSkipBack, { seekBackward(mediaPlayerComponent.value, playing) }),
        Pair(
            if (!playing.value) TablerIcons.PlayerPlay else TablerIcons.PlayerPause,
            { togglePlay(mediaPlayerComponent = mediaPlayerComponent.value, playing = playing) }
        ),
        Pair(TablerIcons.PlayerSkipForward, { seekForward(mediaPlayerComponent.value, playing) }),
        Pair(if(setFullScreen.value) TablerIcons.Minimize else TablerIcons.Maximize, {
            toggleFullScreen()
        })
    )



    for (button in controlButtons) {
        IconButton(
            modifier = modifier,
            onClick = button.value
        ) {
            Icon(button.key, contentDescription = null)
        }
    }
}