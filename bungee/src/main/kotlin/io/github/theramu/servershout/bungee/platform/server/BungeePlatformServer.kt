package io.github.theramu.servershout.bungee.platform.server

import io.github.theramu.servershout.bungee.platform.player.BungeePlatformPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import io.github.theramu.servershout.common.util.ColorUtil
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.config.ServerInfo

/**
 * @author TheRamU
 * @since 2024/8/28 18:24
 */
class BungeePlatformServer(
    override val handle: ServerInfo
) : PlatformServer {
    override val name: String = handle.name
    override val players = handle.players.map { BungeePlatformPlayer(it, this) }

    override fun sendMessage(message: String) {
        sendMessage(ColorUtil.deserializeComponent(message))
    }

    override fun sendMessage(component: Component) {
        players.forEach { it.sendMessage(component) }
    }
}