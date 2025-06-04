package io.github.theramu.servershout.bukkit.listener

import io.github.theramu.servershout.bukkit.platform.player.BukkitPlatformPlayer
import io.github.theramu.servershout.common.ServerShoutApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author TheRamU
 * @since 2024/8/28 21:32
 */
class PlayerEventListener : Listener {

    private val api get() = ServerShoutApi.api
    private val updateChecker get() = api.updateChecker

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        api.removeCache(event.player.uniqueId)
        updateChecker.notifyUpdate(BukkitPlatformPlayer(event.player))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        api.removeCache(event.player.uniqueId)
    }
}