package utils

object StringUtil {

    fun arrayToString(list: Array<String>?, fromIndex: Int, cha: String?): String {
        return arrayToString(list, fromIndex, list?.size ?: 0, cha)
    }

    fun arrayToString(list: Array<String>?, fromIndex: Int, endIndex: Int, cha: String?): String {
        val builder = StringBuilder()
        if (list == null || list.size <= fromIndex) {
            return ""
        } else if (list.size <= 1) {
            return list[0]
        } else {
            builder.append(list[fromIndex])
        }
        var i = 1 + fromIndex
        while (i < list.size && i < endIndex) {
            builder.append(cha).append(list[i])
            i++
        }
        return builder.toString()
    }


    fun listToString(list: List<String>?, cha: String): String {
        val builder = StringBuilder()
        if (list == null || list.isEmpty()) {
            return ""
        } else if (list.size <= 1) {
            return list[0]
        } else {
            builder.append(list[0])
        }
        for (i in 1 until list.size) {
            builder.append(cha).append(list[i])
        }
        return builder.toString()
    }

    fun listToString(list: List<String>?, fromIndex: Int, cha: String?): String {
        val builder = StringBuilder()
        if (list == null || list.size <= fromIndex) {
            return ""
        } else if (list.size <= 1) {
            return list[0]
        } else {
            builder.append(list[fromIndex])
        }
        for (i in fromIndex + 1 until list.size) {
            builder.append(cha).append(list[i])
        }
        return builder.toString()
    }

    fun listToString(list: List<String>?): String {
        return listToString(list, "&&")
    }
}