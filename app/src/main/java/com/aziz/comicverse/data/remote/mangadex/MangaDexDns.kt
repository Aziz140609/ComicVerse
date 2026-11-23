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

    override fun lookup(hostname: String): List<InetAddress> {
        return if (hostname.endsWith("mangadex.org", ignoreCase = true)) {
            mangaDexIps
        } else {
            Dns.SYSTEM.lookup(hostname)
        }
    }
}
