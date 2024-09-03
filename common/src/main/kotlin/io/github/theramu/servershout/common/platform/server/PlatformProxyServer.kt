package io.github.theramu.servershout.common.platform.server

/**
 * @author TheRamU
 * @since 2024/8/27 1:44
 */
interface PlatformProxyServer {
    val handle: Any
    val servers: List<PlatformServer>
}