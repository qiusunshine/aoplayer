package components

import KryerMediaPlayerComponent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import controls.millisToTime

@ExperimentalFoundationApi
@Composable
fun VideoPosition(
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>,
    modifier: Modifier,
    videoPosition: MutableState<Float>
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Slider(
            value = videoPosition.value,
            onValueChange = {
                videoPosition.value = it
                mediaPlayerComponent.value.mediaPlayer().controls().setPosition(it)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary,
                inactiveTrackColor = Color.Gray
            ),
            modifier = modifier.weight(1f),
            enabled = mediaPlayerComponent.value.mediaPlayer().media().isValid
        )

        Text(
            "${mediaPlayerComponent.value.mediaPlayer().status().time().millisToTime()}/${
                mediaPlayerComponent.value.mediaPlayer().status().length().millisToTime()
            }", color = Color.White, modifier = modifier.combinedClickable(onClick = {}, onDoubleClick = {
                println("double clicked")
            })
        )
    }
}