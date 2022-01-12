package views

import KryerMediaPlayerComponent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import components.ControlButtons
import components.VideoPosition
import components.VolumeControl
import compose.icons.TablerIcons
import compose.icons.tablericons.Playlist
import controls.setPlayUrls
import service.model.SwitchRoutes
import utils.ViewUtils

@ExperimentalFoundationApi
@Composable
fun ControlBar(
    modifier: Modifier,
    mediaPlayerComponent: MutableState<KryerMediaPlayerComponent>,
    videoPosition: MutableState<Float>,
    playing: MutableState<Boolean>,
    volume: MutableState<Int>,
    setFullScreen: MutableState<Boolean>,
    toggleFullScreen: () -> Unit,
    showPlaylist: () -> Unit,
    switchRoutes: MutableState<SwitchRoutes>
) {
    val showRoutes = remember { mutableStateOf(false) }

    Surface {
        if (showRoutes.value && switchRoutes.value.routes.size > 1) {
            Dialog(
                onDismissRequest = {
                    showRoutes.value = false
                }, properties = DialogProperties(
                    title = "线路切换",
                    icon = ViewUtils.getIcon(),
                )
            ) {
                Surface(
                    color = Color.DarkGray
                ) {
                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(minSize = 128.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                    ) {
                        items(switchRoutes.value.routes.size) {
                            val pos = it
                            Box(modifier = Modifier.padding(3.dp)) {
                                TextButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        showRoutes.value = false
                                        setPlayUrls(switchRoutes, switchRoutes.value.routes, pos)
                                    }, colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.White,
                                        backgroundColor = Color(0x60000000)
                                    )
                                ) {
                                    val sel = pos + 1
                                    Text(
                                        text = "线路$sel",
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier.fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            VideoPosition(
                modifier = modifier,
                mediaPlayerComponent = mediaPlayerComponent,
                videoPosition = videoPosition
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {


                ControlButtons(
                    playing,
                    mediaPlayerComponent,
                    Modifier,
                    setFullScreen,
                    toggleFullScreen
                )

                Spacer(modifier.weight(1f))
                if (switchRoutes.value.routes.size > 1) {
                    val sel = switchRoutes.value.selected + 1
                    TextButton(
                        onClick = {
                            showRoutes.value = true
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Text(text = "线路$sel", maxLines = 1)
                    }
                }
                VolumeControl(mediaPlayerComponent, modifier.width(200.dp), volume)

                IconButton(
                    onClick = {
                        showPlaylist()
                    },
                    modifier = modifier.background(Color.Transparent)
                ) {
                    Icon(
                        TablerIcons.Playlist, contentDescription = "Toggle Playlist",
                    )
                }
            }
        }
    }
}

