package components

import KryerMediaPlayerComponent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.VolumeDown
import compose.icons.fontawesomeicons.solid.VolumeUp
import controls.MaxVolume
import controls.volumeDown
import controls.volumeUp

@Composable
fun VolumeControl(
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>,
    modifier: Modifier,
    volume: MutableState<Int>
) {
    Row(modifier) {
        val iconModifier = modifier.size(20.dp)
        IconButton(onClick = { volumeDown(mediaPlayerComponent.value) }) {
            Icon(
                FontAwesomeIcons.Solid.VolumeDown,
                contentDescription = null,
                tint = Color.White,
                modifier = iconModifier
            )
        }
        Slider(
            value = volume.value.toFloat(),
            onValueChange = {
                volume.value = it.toInt()
                mediaPlayerComponent.value.mediaPlayer().audio().setVolume(it.toInt())
            },
            valueRange = 0f..MaxVolume.toFloat(),
            modifier = modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary,
                inactiveTrackColor = Color.Gray
            ),
        )
        IconButton(onClick = { volumeUp(mediaPlayerComponent.value) }) {
            Icon(
                FontAwesomeIcons.Solid.VolumeUp,
                contentDescription = null,
                tint = Color.White,
                modifier = iconModifier
            )
        }
    }
}