package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import service.LiveModel
import service.model.LiveItem
import utils.ThreadPool
import utils.ViewUtils

@ExperimentalFoundationApi
@Composable
fun Live(
    playIP: MutableState<String>,
    showLive: MutableState<Boolean>
) {
    val list = remember { mutableStateOf(ArrayList<LiveItem>()) }

    SideEffect {
        GlobalScope.launch(Dispatchers.IO) {
            LiveModel.loadData(null) {
                val data = it
                ThreadPool.runOnUI {
                    list.value = data
                }
            }
        }
    }

    Dialog(
        onDismissRequest = {
            showLive.value = false
        }, properties = DialogProperties(
            title = "直播源",
            icon = ViewUtils.getIcon(),
            size = IntSize(600, 480)
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
                items(list.value.size) {
                    val item = list.value[it]
                    Box(modifier = Modifier.padding(3.dp)) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                playIP.value = item.urls[0]
                                showLive.value = false
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