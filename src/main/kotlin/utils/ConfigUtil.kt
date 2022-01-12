package utils

object ConfigUtil {
    private const val configFile: String = "hiker-config.ini"

    fun put(key: String, value: String) {
        var exist = false
        val configs = FileUtil.fileToString(configFile)
        var newConfigs = ""
        for (line in configs.split("\n")) {
            val key1 = "$key="
            if (line.startsWith(key1)) {
                val newLine = "$key=$value"
                newConfigs = if (newConfigs.isEmpty()) {
                    newLine
                } else {
                    "$newConfigs\n$newLine"
                }
                exist = true
            } else if (line.isNotEmpty()) {
                newConfigs = "$newConfigs\n$line"
            }
        }
        if (!exist) {
            val line = "$key=$value"
            newConfigs = if (newConfigs.isEmpty()) {
                line
            } else {
                "$newConfigs\n$line"
            }
        }
        FileUtil.stringToFile(newConfigs, configFile)
    }

    fun get(key: String, defaultValue: String = ""): String {
        val configs = FileUtil.fileToString(configFile)
        for (line in configs.split("\n")) {
            val key1 = "$key="
            if (line.startsWith(key1)) {
                return line.substring(key1.length)
            }
        }
        return defaultValue
    }
}