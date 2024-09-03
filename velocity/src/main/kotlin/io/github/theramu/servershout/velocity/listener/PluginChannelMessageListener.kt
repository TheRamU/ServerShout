package io.github.theramu.servershout.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.velocity.ServerShoutVelocityApi
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 23:42
 */
class PluginChannelMessageListener {

    private val api get() = ServerShoutApi.api as ServerShoutVelocityApi
    private val balanceService get() = api.balanceService

    @Subscribe
    fun onPluginMessageFromPlayer(event: PluginMessageEvent) {
        if (event.identifier !== api.identifier) return
        DataInputStream(ByteArrayInputStream(event.data)).use { input ->
            val type = input.readUTF()
            if (type == "UPDATE_BALANCE") {
                val uuid = input.readUTF()
                balanceService.removeCache(UUID.fromString(uuid))
            }
        }
    }
}