package com.aziz.comicverse.data.remote.mangadex

import okhttp3.Dns
import java.net.InetAddress

/**
 * Custom DNS resolver that bypasses Indonesian ISP DNS sinkholes (aduankonten.id / Internet Positif)
 * for MangaDex domains by resolving directly to MangaDex official anycast IP addresses.
 */
class MangaDexDns : Dns {
    private val mangaDexIps: List<InetAddress> by lazy {
        listOf(
            InetAddress.getByName("45.129.229.1"),
            InetAddress.getByName("45.129.229.2")
        )
    }

    private val cloudflareIps: List<InetAddress> by lazy {
        listOf(
            InetAddress.getByName("104.26.2.73"),
            InetAddress.getByName("172.67.73.44"),
            InetAddress.getByName("104.26.3.73")
        )
    }

    override fun lookup(hostname: String): List<InetAddress> {
        return try {
            if (hostname.endsWith("mangadex.org", ignoreCase = true)) {
                mangaDexIps
            } else if (hostname.endsWith("mangadex.network", ignoreCase = true)) {
                try {
                    Dns.SYSTEM.lookup(hostname)
                } catch (e: Exception) {
                    cloudflareIps
                }
            } else {
                Dns.SYSTEM.lookup(hostname)
            }
        } catch (e: Exception) {
            if (hostname.contains("mangadex")) mangaDexIps else throw e
        }
    }
}
