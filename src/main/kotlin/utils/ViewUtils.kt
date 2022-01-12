package utils

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object ViewUtils {
    fun getIcon(): BufferedImage {
        var image: BufferedImage? = null
        try {
            val img = Thread
                .currentThread()
                .contextClassLoader
                .getResourceAsStream("windows_icon.png")
            image = ImageIO.read(img)
        } catch (e: Exception) {
            println("couldn't open image")
        }

        if (image == null) {
            image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }
        return image
    }
}