package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controls.setPlayUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import service.LiveModel
import service.model.LiveItem
import service.model.SwitchRoutes
import utils.ConfigUtil
import utils.ThreadPool

@ExperimentalFoundationApi
@Composable
fun Playlist(
    modifier: Modifier,
    switchRoutes: MutableState<SwitchRoutes>,
    showPlaylist: MutableState<Boolean>
) {
    val list = remember { mutableStateOf(ArrayList<LiveItem>()) }
    val liveUrl = remember { mutableStateOf("") }
    val ready = remember { mutableStateOf(false) }

    DisposableEffect(liveUrl.value) {
        GlobalScope.launch(Dispatchers.IO) {
            LiveModel.loadData(liveUrl.value) {
                val data = it
                ThreadPool.runOnUI {
                    list.value = data
                }
            }
        }
        onDispose {

        }
    }

    DisposableEffect(ready.value) {
        if (!ready.value) {
            ready.value = true
            val live = ConfigUtil.get("live", "")
            if (live.isNotEmpty()) {
                liveUrl.value = live
            }
        }
        onDispose { }
    }

    Surface(
        color = Color.DarkGray,
        modifier = modifier
            .defaultMinSize(minWidth = 400.dp)
            .fillMaxHeight()
    ) {
        Column(modifier) {
            Surface(
                modifier = Modifier.width(400.dp).padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                UrlInput(
                    defaultValue = liveUrl.value,
                    onChange = {
                        liveUrl.value = it
                    }
                )
            }
            LazyVerticalGrid(
                cells = GridCells.Adaptive(minSize = 128.dp),
                modifier = Modifier.fillMaxHeight().width(400.dp),
                contentPadding = PaddingValues(10.dp),
            ) {
                items(list.value.size) {
                    val item = list.value[it]
                    Box(modifier = Modifier.padding(3.dp)) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                setPlayUrls(switchRoutes, item.urls)
                                showPlaylist.value = false
                            }, colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White,
                                backgroundColor = Color(0x60000000)
                            )
                        ) {
                            Text(
                                text = item.name,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UrlInput(
    defaultValue: String,
    onChange: (url: String) -> Unit
) {
    val text = remember { mutableStateOf(defaultValue) }
    DisposableEffect(defaultValue) {
        text.value = defaultValue
        onDispose { }
    }
    TextField(
        value = text.value,
        placeholder = @Composable {
            Text("请输入直播源地址")
        },
        onValueChange = {
            if (it.endsWith("\n") || it.endsWith("\r")) {
                val url = it.substring(0, it.length - 1)
                onChange(url)
            } else {
                text.value = it
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small.copy(CornerSize(5.dp)),
    )
}