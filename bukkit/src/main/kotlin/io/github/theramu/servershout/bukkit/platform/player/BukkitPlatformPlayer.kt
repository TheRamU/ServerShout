package io.github.theramu.servershout.bukkit.platform.player

import io.github.theramu.servershout.common.platform.player.PlatformPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/27 1:22
 */
class BukkitPlatformPlayer(
    override val handle: Player
) : PlatformPlayer {

    override val name: String = handle.name
    override val uuid: UUID = handle.uniqueId

    override fun sendMessage(message: String) {
        handle.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    override fun sendMessage(component: Component) {
        handle.sendRawMessage(JSONComponentSerializer.json().serialize(component))
    }

    override fun hasPermission(permission: String) = handle.hasPermission(permission)

    override fun isOnline() = handle.isOnline
}