package io.github.theramu.servershout.bukkit.listener

import io.github.theramu.servershout.common.ServerShoutApi
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.*


/**
 * @author TheRamU
 * @since 2024/8/28 23:40
 */
class PluginChannelMessageListener : PluginMessageListener {

    private val api get() = ServerShoutApi.api
    private val balanceService get() = api.balanceService

    override fun onPluginMessageReceived(channel: String, player: Player, args: ByteArray) {
        if (channel != "servershout:main") return
        DataInputStream(ByteArrayInputStream(args)).use { input ->
            val type = input.readUTF()
            if (type == "UPDATE_BALANCE") {
                val uuid = input.readUTF()
                balanceService.removeCache(UUID.fromString(uuid))
            }
        }
    }
}