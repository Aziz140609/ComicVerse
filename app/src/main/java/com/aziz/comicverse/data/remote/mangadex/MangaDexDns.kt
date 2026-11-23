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
        val lower = hostname.lowercase()
        return when {
            lower.endsWith("mangadex.org") -> mangaDexIps
            lower.endsWith("mangadex.network") -> cloudflareIps
            else -> {
                try {
                    Dns.SYSTEM.lookup(hostname)
                } catch (e: Exception) {
                    if (lower.contains("mangadex")) mangaDexIps else throw e
                }
            }
        }
    }
}
