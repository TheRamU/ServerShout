package io.github.theramu.servershout.bungee.platform

import io.github.theramu.servershout.bungee.platform.command.BungeePlatformConsoleCommandSender
import io.github.theramu.servershout.bungee.platform.plugin.BungeePlatformPluginManager
import io.github.theramu.servershout.bungee.platform.scheduler.BungeePlatformScheduler
import io.github.theramu.servershout.bungee.platform.server.BungeePlatformProxyServer
import io.github.theramu.servershout.common.platform.ProxyPlatform
import net.md_5.bungee.api.ProxyServer

/**
 * @author TheRamU
 * @since 2024/9/1 0:55
 */
class BungeePlatform : ProxyPlatform {
    override val proxyServer = BungeePlatformProxyServer(ProxyServer.getInstance())
    override val onlinePlayers get() = proxyServer.servers.flatMap { it.players }
    override val scheduler = BungeePlatformScheduler()
    override val consoleCommandSender = BungeePlatformConsoleCommandSender(ProxyServer.getInstance().console)
    override val pluginManager = BungeePlatformPluginManager()
}