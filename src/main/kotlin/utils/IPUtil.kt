package utils

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList


object IPUtil {

    fun getIP(): List<String> {
        val list = ArrayList<InetAddress>()
        // 遍历所有的网络接口
        val ifaces: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
        while (ifaces.hasMoreElements()) {
            val iface = ifaces.nextElement() as NetworkInterface
            // 在所有的接口下再遍历IP
            val inetAddrs: Enumeration<*> = iface.inetAddresses
            while (inetAddrs.hasMoreElements()) {
                val inetAddr = inetAddrs.nextElement() as InetAddress
                if (!inetAddr.isLoopbackAddress && inetAddr is Inet4Address) { // 排除loopback类型地址
                    if (!inetAddr.hostAddress.contains(":")) {
                        list.add(inetAddr)
                    }
                }
            }
        }
        return list.map { it.hostAddress }
    }
}