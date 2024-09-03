package io.github.theramu.servershout.bungee.listener

import io.github.theramu.servershout.bungee.platform.player.BungeePlatformPlayer
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.ServerShoutProxyApi
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

/**
 * @author TheRamU
 * @since 2024/8/28 21:36
 */
class PlayerEventListener : Listener {

    private val api get() = ServerShoutApi.api as ServerShoutProxyApi
    private val balanceService get() = api.balanceService
    private val updateChecker get() = api.updateChecker
    private val shoutChannelService get() = api.shoutChannelService

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChat(event: ChatEvent) {
        val sender = event.sender
        if (sender !is ProxiedPlayer) return
        val valid = shoutChannelService.shout(BungeePlatformPlayer(sender), event.message)
        if (valid) event.isCancelled = true
    }

    @EventHandler
    fun onServerSwitch(event: ServerSwitchEvent) {
        updateChecker.notifyUpdate(BungeePlatformPlayer(event.player))
    }

    @EventHandler
    fun onPlayerDisconnect(event: PlayerDisconnectEvent) {
        val uuid = event.player.uniqueId
        balanceService.removeCache(uuid)
        updateChecker.removeNotified(uuid)
        shoutChannelService.removePlayer(uuid)
    }
}