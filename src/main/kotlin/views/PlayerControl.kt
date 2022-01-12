package views

import KryerMediaPlayerComponent
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
    val currentWindow = LocalAppWindow.current
    val showControlBar = remember { mutableStateOf(true) }
    var inited = false
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
    mediaPlayerComponent.value.playToggleAction = { togglePlay(mediaPlayerComponent.value, playing) }
    mediaPlayerComponent.value.seekForwardAcion = { seekForward(mediaPlayerComponent.value, playing) }
    mediaPlayerComponent.value.seekBackWardAction = { seekBackward(mediaPlayerComponent.value, playing) }
    mediaPlayerComponent.value.volumeUpAction = { volumeUp(mediaPlayerComponent.value) }
    mediaPlayerComponent.value.volumeDownAction = { volumeDown(mediaPlayerComponent.value) }

    val readyToPlay = remember { mutableStateOf(false) }
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
                        val ok = mediaPlayerComponent.value.mediaPlayer().media().play(pu, *arr)
                        Timber.d("playOK: $ok")
                    } else {
                        Timber.d("playMe start without options")
                        val ok = mediaPlayerComponent.value.mediaPlayer().media().play(pu)
                        Timber.d("playOK: $ok")
                    }
                } else {
                    Timber.d("playMe start without headers")
                    val pu = playUrl0.replace("\n", "")?.replace("\r", "")
                    val ok = mediaPlayerComponent.value.mediaPlayer().media().play(pu)
                    Timber.d("playOK: $ok")
                }
            } catch (e: Exception) {
                Timber.d("playMe error: " + e.message)
            }
        }
    }

    DisposableEffect(readyToPlay.value) {
        if (readyToPlay.value && RemoteServerHolder.url == null && !inited) {
            inited = true
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
        onDispose {

        }
    }

    DisposableEffect(setFullScreen.value) {
        showControlBar.value = !setFullScreen.value
        onDispose {

        }
    }

    DisposableEffect(getPlayUrl(switchRoutes)) {
        val playUrl = getPlayUrl(switchRoutes)
        if (playUrl.isNotEmpty() && (playUrl.contains(".mp4") || playUrl.contains(".m3u8"))) {
            RemoteServerHolder.url = playUrl.replace("\n", "").replace("\r", "")
            val ok = mediaPlayerComponent.value.mediaPlayer().media().play(RemoteServerHolder.url)
            Timber.d("playOK: $ok")
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
        onDispose {

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
                mediaPlayerComponent, readyToPlay
            )
            if (showLive.value) {
                Playlist(modifier, switchRoutes, showLive)
            }
        }
        if (showControlBar.value && readyToPlay.value) {
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


