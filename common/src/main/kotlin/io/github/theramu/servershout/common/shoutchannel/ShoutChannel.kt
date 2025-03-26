package io.github.theramu.servershout.common.shoutchannel

import io.github.theramu.servershout.common.config.settings.ServerListSettings
import io.github.theramu.servershout.common.platform.player.PlatformProxyPlayer
import java.io.Serializable

/**
 * @author TheRamU
 * @since 2024/8/20 22:15
 */
data class ShoutChannel(
    var name: String?,
    val enabled: Boolean,
    val chatPrefix: String,
    val permission: String,
    val colorPermission: String,
    val allowEmptyMessage: Boolean,
    val tokenCostFull: TokenCost,
    val tokenCostEmpty: TokenCost,
    val localBroadcast: Boolean,
    val senderServerList: ServerListSettings,
    val receiverServerList: ServerListSettings,
    val cooldown: Int,
    val expiration: Int,
    val formatEmpty: List<String>,
    val formatFull: List<String>
) : Serializable {

    companion object {
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun deserialize(map: Map<String, Any>) = ShoutChannel(
            name = null,
            enabled = map["enabled"] as Boolean,
            chatPrefix = map["chat-prefix"] as String,
            permission = map["permission"] as String,
            colorPermission = map["color-permission"] as String,
            allowEmptyMessage = map["allow-empty-message"] as Boolean,
            tokenCostFull = TokenCost(map["token-cost-full"] as String),
            tokenCostEmpty = TokenCost(map["token-cost-empty"] as String),
            localBroadcast = map["local-broadcast"] as Boolean,
            senderServerList = ServerListSettings.deserialize(map["sender-server-list"] as Map<String, Any>),
            receiverServerList = ServerListSettings.deserialize(map["receiver-server-list"] as Map<String, Any>),
            cooldown = map["cooldown"] as Int,
            expiration = map["expiration"] as Int,
            formatEmpty = map["format-empty"] as List<String>,
            formatFull = map["format-full"] as List<String>
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoutChannel

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun hasPermission(player: PlatformProxyPlayer): Boolean {
        return permission.isEmpty() || player.hasPermission(permission)
    }

    fun hasColorPermission(player: PlatformProxyPlayer): Boolean {
        return colorPermission.isEmpty() || player.hasPermission(colorPermission)
    }

    class TokenCost(notation: String) {
        val name: String
        val amount: Long

        init {
            try {
                val split = notation.split(":")
                if (split.size != 2) {
                    throw IllegalArgumentException("Invalid notation format. Expected 'name:amount', got: $notation")
                }
                name = split[0]
                amount = split[1].toLong()
                if (amount < 0) {
                    throw IllegalArgumentException("Invalid amount. Expected a positive integer, got: $amount")
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid amount format. Expected an integer, got: ${notation.split(":")[1]}", e)
            }
        }
    }
}