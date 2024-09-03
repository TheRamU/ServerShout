package io.github.theramu.servershout.bungee.platform.player

import io.github.theramu.servershout.bungee.platform.server.BungeePlatformServer
import io.github.theramu.servershout.common.platform.player.PlatformProxyPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.chat.ComponentSerializer
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 18:25
 */
class BungeePlatformPlayer : PlatformProxyPlayer {

    override val handle: ProxiedPlayer
    override val currentServer: BungeePlatformServer
    override val name: String
    override val uuid: UUID

    constructor(handle: ProxiedPlayer) : this(handle, BungeePlatformServer(handle.server.info))

    constructor(handle: ProxiedPlayer, currentServer: BungeePlatformServer) {
        this.handle = handle
        this.currentServer = currentServer
        this.name = handle.name
        this.uuid = handle.uniqueId
    }

    @Suppress("DEPRECATION")
    override fun sendMessage(message: String) {
        handle.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    override fun sendMessage(component: Component) {
        ComponentSerializer.parse(GsonComponentSerializer.gson().serialize(component)).forEach { handle.sendMessage(it) }
    }

    override fun connect(server: PlatformServer) {
        if (server !is BungeePlatformServer) throw IllegalArgumentException("Invalid server type")
        handle.connect(server.handle)
    }

    override fun hasPermission(permission: String) = handle.hasPermission(permission)

    override fun isOnline() = handle.isConnected
}