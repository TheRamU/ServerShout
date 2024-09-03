package io.github.theramu.servershout.bungee.platform.server

import io.github.theramu.servershout.common.platform.server.PlatformProxyServer
import net.md_5.bungee.api.ProxyServer

/**
 * @author TheRamU
 * @since 2024/8/28 18:23
 */
class BungeePlatformProxyServer(
    override val handle: ProxyServer
) : PlatformProxyServer {
    override val servers get() = handle.servers.values.map { BungeePlatformServer(it) }
}