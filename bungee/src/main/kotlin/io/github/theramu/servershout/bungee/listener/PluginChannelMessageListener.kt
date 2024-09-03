package io.github.theramu.servershout.bungee.listener

import io.github.theramu.servershout.common.ServerShoutApi
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 23:45
 */
class PluginChannelMessageListener : Listener {

    private val api get() = ServerShoutApi.api
    private val balanceService get() = api.balanceService

    @EventHandler
    fun onPluginMessage(event: PluginMessageEvent) {
        if (event.tag != "servershout:main") return
        DataInputStream(ByteArrayInputStream(event.data)).use { input ->
            val type = input.readUTF()
            if (type == "UPDATE_BALANCE") {
                val uuid = input.readUTF()
                balanceService.removeCache(UUID.fromString(uuid))
            }
        }
    }
}