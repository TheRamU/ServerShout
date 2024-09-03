package io.github.theramu.servershout.velocity.platform.player

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import io.github.theramu.servershout.common.platform.player.PlatformProxyPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import io.github.theramu.servershout.common.util.ColorUtil
import io.github.theramu.servershout.velocity.ServerShoutVelocityPlugin
import io.github.theramu.servershout.velocity.platform.server.VelocityPlatformServer
import net.kyori.adventure.text.Component
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/27 1:15
 */
class VelocityPlatformPlayer : PlatformProxyPlayer {

    override val handle: Player
    override val currentServer: VelocityPlatformServer
    override val name: String
    override val uuid: UUID

    constructor(handle: Player) : this(handle, VelocityPlatformServer(handle.currentServer()))

    constructor(handle: Player, currentServer: VelocityPlatformServer) {
        this.handle = handle
        this.currentServer = currentServer
        this.name = handle.username
        this.uuid = handle.uniqueId
    }

    override fun sendMessage(message: String) {
        handle.sendMessage(ColorUtil.deserializeComponent(message))
    }

    override fun sendMessage(component: Component) {
        handle.sendMessage(component)
    }

    override fun connect(server: PlatformServer) {
        if (server !is VelocityPlatformServer) throw IllegalArgumentException("Invalid server type")
        handle.createConnectionRequest(server.handle).connect()
    }

    override fun hasPermission(permission: String) = handle.hasPermission(permission)

    override fun isOnline() = handle.isActive

    companion object {
        private fun Player.currentServer(): RegisteredServer {
            return ServerShoutVelocityPlugin.getProxy().getServer(this.currentServer.get().serverInfo.name).get()
        }
    }
}