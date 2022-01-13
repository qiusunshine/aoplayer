package views

import KryerMediaPlayerComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import controls.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import service.RemoteServerHolder
import service.model.SwitchRoutes
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy
import uk.co.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy
import uk.co.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy
import utils.*

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun PlayerControl(
    setFullScreen: MutableState<Boolean>,
    scanning: MutableState<Boolean>,
    showLive: MutableState<Boolean>,
    switchRoutes: MutableState<SwitchRoutes>
) {
    NativeDiscovery(
        WindowsNativeDiscoveryStrategy(),
        DiscoveryStrategy(),
        LinuxNativeDiscoveryStrategy(),
        OsxNativeDiscoveryStrategy()
    ).discover()

    val scope = rememberCoroutineScope()
    val currentWindow = LocalAppWindow.current
    val showControlBar = remember { mutableStateOf(true) }
    val modifier = Modifier
    modifier.background(Color.Black)
    val videoPosition = remember { mutableStateOf(0f) }
    val volume = remember { mutableStateOf(100) }
    val playing = remember { mutableStateOf(false) }

    val mediaPlayerComponent =
        remember {
            mutableStateOf(
                KryerMediaPlayerComponent(
                    videoPosition,
                    playing,
                    setFullScreen,
                    currentWindow,
                    volume,
                    showControlBar
                )
            )
        }

    fun playAsync(url: String, vararg ops: String) {
        try {
            val start = System.currentTimeMillis()
            val ok = mediaPlayerComponent.value.mediaPlayer().media().play(url, *ops)
            val consume = System.currentTimeMillis() - start
            Timber.d("LaunchedEffect playOK: $ok, consume: $consume")
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.d("LaunchedEffect play error")
        }
    }

    val playMe = { playUrl0: String ->
        if (RemoteServerHolder.playUrl != playUrl0) {
            RemoteServerHolder.playUrl = playUrl0
            Timber.d("playMe start $playUrl0")
            try {
                if (playUrl0.startsWith("{")) {
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    val obj = json.parseToJsonElement(playUrl0) as JsonObject
                    val options: MutableList<String> = arrayListOf()
                    if (obj.containsKey("headers")) {
                        val headers = obj["headers"] as JsonObject
                        options.add(":http-reconnect")
                        if (headers.containsKey("Referer")) {
                            options.add(":http-referrer=" + headers["Referer"]!!.jsonPrimitive.content)
                        }
                        if (headers.containsKey("User-Agent")) {
                            options.add(":http-user-agent=" + headers["User-Agent"]!!.jsonPrimitive.content)
                        }
                    }
                    val pu = obj["url"]?.jsonPrimitive?.content?.replace("\n", "")?.replace("\r", "")
                    println(pu)
                    if (options.size > 0) {
                        Timber.d("playMe start with options")
                        val arr = options.toTypedArray()
                        playAsync(pu!!, *arr)
                    } else {
                        Timber.d("playMe start without options")
                        playAsync(pu!!)
                    }
                } else {
                    Timber.d("playMe start without headers")
                    val pu = playUrl0.replace("\n", "")?.replace("\r", "")
                    playAsync(pu)
                }
            } catch (e: Exception) {
                Timber.d("playMe error: " + e.message)
            }
        }
    }

    LaunchedEffect(mediaPlayerComponent.value) {
        mediaPlayerComponent.value.playToggleAction = { togglePlay(mediaPlayerComponent.value, playing) }
        mediaPlayerComponent.value.seekForwardAcion = { seekForward(mediaPlayerComponent.value, playing) }
        mediaPlayerComponent.value.seekBackWardAction = { seekBackward(mediaPlayerComponent.value, playing) }
        mediaPlayerComponent.value.volumeUpAction = { volumeUp(mediaPlayerComponent.value) }
        mediaPlayerComponent.value.volumeDownAction = { volumeDown(mediaPlayerComponent.value) }
    }
    LaunchedEffect(Unit) {
        if (RemoteServerHolder.url == null) {
            scanning.value = true
            val remote = ConfigUtil.get("remote", "")
            if (remote.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    ScanDeviceUtil.startCheckPlayUrl(remote, "") {
                        Timber.d("url change: $it")
                        RemoteServerHolder.url = remote
                        setPlayUrl(switchRoutes, remote)
                        playMe(it)
                    }
                }
            }
            ScanDeviceUtil { url, playUrl ->
                ConfigUtil.put("remote", url)
                ThreadPool.runOnUI {
                    scanning.value = false
                    RemoteServerHolder.url = url
                    setPlayUrl(switchRoutes, url)
                    playMe(playUrl)
                    GlobalScope.launch(Dispatchers.IO) {
                        ScanDeviceUtil.startCheckPlayUrl(url, playUrl) {
                            Timber.d("url change: $it")
                            playMe(it)
                        }
                    }
                }
            }.scan()
        }
    }

    LaunchedEffect(setFullScreen.value) {
        showControlBar.value = !setFullScreen.value
    }

    LaunchedEffect(getPlayUrl(switchRoutes)) {
        val playUrl = getPlayUrl(switchRoutes)
        if (playUrl.isNotEmpty() && (playUrl.contains(".mp4") || playUrl.contains(".m3u8"))) {
            RemoteServerHolder.url = playUrl.replace("\n", "").replace("\r", "")
            playAsync(RemoteServerHolder.url!!)
        } else if (playUrl.endsWith(":52020")) {
            GlobalScope.launch(Dispatchers.IO) {
                ConfigUtil.put("remote", playUrl)
                val pu = if (RemoteServerHolder.playUrl == null) "" else RemoteServerHolder.playUrl!!
                ScanDeviceUtil.startCheckPlayUrl(playUrl, pu) {
                    Timber.d("url change2: $it")
                    playMe(it)
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(modifier.weight(1f).fillMaxWidth().background(Color.DarkGray)) {
            PlayerFrame(
                modifier
                    .weight(1f)
                    .fillMaxHeight(),
                mediaPlayerComponent
            )

            AnimatedVisibility(
                visible = showLive.value,
                modifier = Modifier.background(Color.Transparent)
            ) {

                Playlist(modifier, switchRoutes, showLive)
            }
        }

        AnimatedVisibility(
            visible = showControlBar.value,
            modifier = Modifier.background(Color.Transparent)
        ) {
            ControlBar(
                modifier,
                mediaPlayerComponent,
                videoPosition,
                volume = volume,
                playing = playing,
                setFullScreen = setFullScreen,
                toggleFullScreen = {
                    setFullScreen.value = !setFullScreen.value
                },
                showPlaylist = {
                    showLive.value = !showLive.value
                },
                switchRoutes = switchRoutes
            )
        }
    }
}


