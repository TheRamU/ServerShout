package io.github.theramu.servershout.common.platform

import io.github.theramu.servershout.common.platform.server.PlatformProxyServer

/**
 * @author TheRamU
 * @since 2024/9/1 0:51
 */
interface ProxyPlatform : Platform {
    val proxyServer: PlatformProxyServer
}