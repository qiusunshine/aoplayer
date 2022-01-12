package utils

object Timber {
    val logs = ArrayList<String?>()
    fun d(msg: String?) {
        println(msg)
        logs.add(msg)
    }

    fun d(tag: String, msg: String) {
        println("$tag:$msg")
        logs.add("$tag:$msg")
    }
}