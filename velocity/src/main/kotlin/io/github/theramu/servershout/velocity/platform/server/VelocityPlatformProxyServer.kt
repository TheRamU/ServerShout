package io.github.theramu.servershout.velocity.platform.server

import com.velocitypowered.api.proxy.ProxyServer
import io.github.theramu.servershout.common.platform.server.PlatformProxyServer

/**
 * @author TheRamU
 * @since 2024/8/27 1:47
 */
class VelocityPlatformProxyServer(
    override val handle: ProxyServer
) : PlatformProxyServer {
    override val servers get() = handle.allServers.map { VelocityPlatformServer(it) }
}