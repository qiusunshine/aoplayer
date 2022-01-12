import androidx.compose.desktop.AppWindow
import androidx.compose.runtime.MutableState
import controls.toggleFullScreen
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import utils.Timber
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class KryerMediaPlayerComponent(
    private val position: MutableState<Float>,
    private val playing: MutableState<Boolean>,
    private val setFullScreen: MutableState<Boolean>,
    private val parentWindow: AppWindow,
    private val volume: MutableState<Int>,
    private val showControlBar: MutableState<Boolean>
) : EmbeddedMediaPlayerComponent() {

    var volumeDownAction = {}
    var volumeUpAction = {}

    private var lastX = 0
    private var lastY = 0
    var playToggleAction = {}
    var seekForwardAcion = {}
    var seekBackWardAction = {}


    override fun playing(mediaPlayer: MediaPlayer?) {
        super.playing(mediaPlayer)
        playing.value = true
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        super.paused(mediaPlayer)
        playing.value = false
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        Timber.d("error:")
        super.error(mediaPlayer)
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        super.stopped(mediaPlayer)
        paused(mediaPlayer)
    }

    override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
        super.positionChanged(mediaPlayer, newPosition)
        position.value = newPosition
    }

    override fun mouseClicked(e: MouseEvent?) {
        super.mouseClicked(e)
        if (e?.clickCount == 2 && e.button == MouseEvent.BUTTON1) {
            Timber.d("toggling fullscreen")
            toggleFullScreen(setFullScreen)
        } else if (e?.clickCount == 1 && e.button == MouseEvent.BUTTON1) {
            if (setFullScreen.value) {
                showControlBar.value = !showControlBar.value
            }
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        super.mousePressed(e)
        if (e == null) {
            return
        }

        lastX = e.x
        lastY = e.y
    }

    override fun mouseDragged(e: MouseEvent?) {
        super.mouseDragged(e)
        if (e == null) return

        val x = parentWindow.x
        val y = parentWindow.y
        parentWindow.setLocation(x - lastX + e.x, y - lastY + e.y)
    }

    override fun mouseMoved(e: MouseEvent?) {
        super.mouseMoved(e)
    }

    override fun keyPressed(e: KeyEvent?) {
        super.keyPressed(e)
        if (e == null) {
            return
        }
        println(e.keyCode)
        when (e.keyCode) {
            32 -> playToggleAction()
            39 -> seekForwardAcion()
            37 -> seekBackWardAction()
            38 -> volumeUpAction()
            40 -> volumeDownAction()
            10 -> toggleFullScreen(setFullScreen)
            KeyEvent.VK_ESCAPE -> setFullScreen.value = false
        }
    }

    override fun volumeChanged(mediaPlayer: MediaPlayer?, volume: Float) {
        super.volumeChanged(mediaPlayer, volume)
        this.volume.value = (volume * 100).toInt()
    }
}