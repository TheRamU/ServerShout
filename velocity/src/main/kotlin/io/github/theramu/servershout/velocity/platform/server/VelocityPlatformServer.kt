package io.github.theramu.servershout.velocity.platform.server

import com.velocitypowered.api.proxy.server.RegisteredServer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import io.github.theramu.servershout.common.util.ColorUtil
import io.github.theramu.servershout.velocity.platform.player.VelocityPlatformPlayer
import net.kyori.adventure.text.Component

/**
 * @author TheRamU
 * @since 2024/8/27 1:30
 */
class VelocityPlatformServer(
    override val handle: RegisteredServer
) : PlatformServer {
    override val name: String = handle.serverInfo.name
    override val players = handle.playersConnected.map { VelocityPlatformPlayer(it, this) }

    override fun sendMessage(message: String) {
        handle.sendMessage(ColorUtil.deserializeComponent(message))
    }

    override fun sendMessage(component: Component) {
        handle.sendMessage(component)
    }
}