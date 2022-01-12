package utils

import kotlinx.coroutines.delay
import okhttp3.*
import utils.IPUtil.getIP
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 作者：By hdy
 * 日期：On 2019/3/30
 * 时间：At 23:33
 */
class ScanDeviceUtil(
    private val consumer: (url: String, playUrl: String) -> Unit
) {
    private var hasFound = AtomicBoolean(false)

    /**
     * 扫描局域网内ip，找到对应服务器
     *
     * @return void
     */
    fun scan() {
        val savedAddr = ConfigUtil.get("remote", "")
        if (savedAddr.isNotEmpty()) {
            val request = Request.Builder()
                .get()
                .url(savedAddr + PLAY_URL)
                .build()
            okHttpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        reScan()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            response.body().use { body ->
                                val s = body!!.string()
                                if (s.isNotEmpty()) {
                                    Timber.d("ScanDeviceUtil ok：%s", savedAddr)
                                    hasFound.set(true)
                                    consumer(savedAddr, s)
                                } else {
                                    reScan()
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            reScan()
                        }
                    }
                })
            return
        } else {
            reScan()
        }
    }

    private fun reScan() {
        ThreadPool.launch {
            // 本机IP地址-完整
            val mDevAddress = getIP() // 获取本机IP地址
            // 局域网IP地址头,如：192.168.1.
            for (address in mDevAddress) {
                val mLocAddress = getLocAddrIndex(address)
                Timber.d("start Ip：%s", address)
                for (i in 1..254) {
                    val currentIp = mLocAddress + i
                    if (address == currentIp) {
                        // 如果与本机IP地址相同,跳过;
                        continue
                    }
                    ThreadPool.launch(MyRunnable(currentIp))
//                    ThreadPool.launch {
//                        try {
//                            val addr = InetAddress.getByName(currentIp)
//                            if (addr.isReachable(1000)) {
//                                println("Available: " + addr.hostAddress)
//                                ThreadPool.launch(MyRunnable(currentIp))
//                            }
//                        } catch (e: IOException) {
//                        }
//                    }
                }
            }
        }
    }

    /**
     * 获取本机IP前缀
     *
     * @param devAddress // 本机IP地址
     * @return String
     */
    private fun getLocAddrIndex(devAddress: String): String? {
        return if (devAddress != "") {
            devAddress.substring(0, devAddress.lastIndexOf(".") + 1)
        } else null
    }

    internal inner class MyRunnable(private val currentIp: String) : Runnable {
        override fun run() {
            if (hasFound.get()) {
                Timber.d("ScanDeviceUtil hasFound, stop load")
                return
            }
            val url = HTTP + currentIp + PORT
//            Timber.d("ScanDeviceUtil start scan url: %s", url)
            val request = Request.Builder()
                .get()
                .url(url + PLAY_URL)
                .build()
            okHttpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            response.body().use { body ->
                                val s = body!!.string()
                                if (s.isNotEmpty()) {
                                    Timber.d("ScanDeviceUtil ok：%s", url)
                                    if (!hasFound.get()) {
                                        hasFound.set(true)
                                        for (call1 in okHttpClient.dispatcher().queuedCalls()) {
                                            call1.cancel()
                                        }
                                        for (call1 in okHttpClient.dispatcher().runningCalls()) {
                                            call1.cancel()
                                        }
                                        consumer(url, s)
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    companion object {
        const val PORT = ":52020"
        const val HTTP = "http://"
        const val PLAY_URL = "/playUrl?enhance=true"
        var okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(2000, TimeUnit.MILLISECONDS)
            .writeTimeout(2000, TimeUnit.MILLISECONDS)
            .connectTimeout(2000, TimeUnit.MILLISECONDS)
            .build()
        var ipUrl: String? = null
        var isChecking: Boolean = false

        suspend fun startCheckPlayUrl(url: String, playUrl: String, consumer: (u: String) -> Unit) {
            if (isChecking) {
                ipUrl = url
            } else {
                ipUrl = url
                startCheckPlayUrl0(playUrl, consumer)
            }
        }

        private suspend fun startCheckPlayUrl0(playUrl: String, consumer: (u: String) -> Unit) {
            isChecking = true
            var playNew = playUrl
            try {
                val request = Request.Builder()
                    .get()
                    .url(ipUrl + PLAY_URL)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    try {
                        response.body().use { body ->
                            val s = body!!.string()
                            if (s.isNotEmpty() && s != playUrl) {
                                playNew = s
                                ThreadPool.runOnUI {
                                    consumer(playNew)
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            } finally {
                delay(1000)
                startCheckPlayUrl0(playNew, consumer)
            }
        }
    }
}