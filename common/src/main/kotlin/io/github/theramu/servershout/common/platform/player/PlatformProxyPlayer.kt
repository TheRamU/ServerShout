package io.github.theramu.servershout.common.platform.player

import io.github.theramu.servershout.common.platform.server.PlatformServer

/**
 * @author TheRamU
 * @since 2024/8/25 2:27
 */
interface PlatformProxyPlayer : PlatformPlayer {
    val currentServer: PlatformServer
    fun connect(server: PlatformServer)
}