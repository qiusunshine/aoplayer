
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import utils.ViewUtils
import views.AppLayout

@ExperimentalAnimationApi
@ExperimentalFoundationApi
fun main(args: Array<String>) =
    Window(
        title = "AO Player",
        resizable = true,
        undecorated = true,
        icon = ViewUtils.getIcon()
    ) {
        AppLayout(args)
    }