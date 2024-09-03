package io.github.theramu.servershout.velocity.platform

import com.velocitypowered.api.proxy.ProxyServer
import io.github.theramu.servershout.common.platform.ProxyPlatform
import io.github.theramu.servershout.velocity.platform.command.VelocityPlatformConsoleCommandSender
import io.github.theramu.servershout.velocity.platform.plugin.VelocityPlatformPluginManager
import io.github.theramu.servershout.velocity.platform.scheduler.VelocityPlatformScheduler
import io.github.theramu.servershout.velocity.platform.server.VelocityPlatformProxyServer

/**
 * @author TheRamU
 * @since 2024/9/1 0:57
 */
class VelocityPlatform(
    proxy: ProxyServer
) : ProxyPlatform {
    override val proxyServer = VelocityPlatformProxyServer(proxy)
    override val onlinePlayers get() = proxyServer.servers.flatMap { it.players }
    override val scheduler = VelocityPlatformScheduler()
    override val consoleCommandSender = VelocityPlatformConsoleCommandSender(proxy.consoleCommandSource)
    override val pluginManager = VelocityPlatformPluginManager()
}