package utils

import uk.co.caprica.vlcj.binding.LibC
import uk.co.caprica.vlcj.binding.RuntimeUtil
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy
import java.io.File

class DiscoveryStrategy : NativeDiscoveryStrategy {

    override fun supported(): Boolean {
        return RuntimeUtil.isWindows()
    }

    override fun discover(): String? {
        val dir = File(System.getProperty("user.dir"))
        val app = dir.absolutePath + File.separator + "app"
        if (File(app + File.separator + "libvlc.dll").exists()) {
            return app
        }
        if (File(dir.absolutePath + File.separator + "libvlc.dll").exists()) {
            return dir.absolutePath
        }
        if (File(dir.absolutePath + File.separator + "vlc" + File.separator + "libvlc.dll").exists()) {
            return dir.absolutePath + File.separator + "vlc"
        }
        return null
    }

    override fun onFound(path: String?): Boolean {
        return true
    }

    override fun onSetPluginPath(path: String?): Boolean {
        var p = path
        if (path != null && File(path).list()?.size == 3 && File(path + File.separator + "plugins").exists()) {
            p = path + File.separator + "plugins"
        }
        return LibC.INSTANCE._putenv(
            String.format(
                "%s=%s",
                "VLC_PLUGIN_PATH",
                p
            )
        ) == 0
    }
}
