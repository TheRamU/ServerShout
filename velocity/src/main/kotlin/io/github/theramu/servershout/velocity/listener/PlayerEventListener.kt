package io.github.theramu.servershout.velocity.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.velocity.ServerShoutVelocityApi
import io.github.theramu.servershout.velocity.platform.player.VelocityPlatformPlayer

/**
 * @author TheRamU
 * @since 2024/08/20 01:29
 */
class PlayerEventListener {

    private val api get() = ServerShoutApi.api as ServerShoutVelocityApi
    private val scheduler get() = api.platform.scheduler
    private val balanceService get() = api.balanceService
    private val updateChecker get() = api.updateChecker
    private val shoutChannelService get() = api.shoutChannelService

    @Subscribe(order = PostOrder.LAST)
    fun onPlayerChat(event: PlayerChatEvent) {
        val valid = this.handleChat(event.player, event.message)
        if (valid && event.result.isAllowed) {
            event.result = PlayerChatEvent.ChatResult.denied()
        }
    }

    @Subscribe(order = PostOrder.LAST)
    fun onPlayerCommand(event: CommandExecuteEvent) {
        val player = event.commandSource as? Player ?: return
        val valid = this.handleChat(player, "/${event.command}")
        if (valid && event.result.isAllowed) {
            event.result = CommandExecuteEvent.CommandResult.denied()
        }
    }

    @Subscribe
    fun onServerSwitch(event: ServerConnectedEvent) {
        if (!event.previousServer.isPresent) return
        val player = event.player
        scheduler.run({
            if (!player.isActive || !player.currentServer.isPresent) return@run
            updateChecker.notifyUpdate(VelocityPlatformPlayer(player))
        }, 100)
    }

    @Subscribe(order = PostOrder.LAST)
    fun onPlayerDisconnect(event: DisconnectEvent) {
        val uuid = event.player.uniqueId
        balanceService.removeCache(uuid)
        updateChecker.removeNotified(uuid)
        shoutChannelService.removePlayer(uuid)
    }

    private fun handleChat(player: Player, message: String): Boolean {
        return shoutChannelService.shout(VelocityPlatformPlayer(player), message)
    }
}