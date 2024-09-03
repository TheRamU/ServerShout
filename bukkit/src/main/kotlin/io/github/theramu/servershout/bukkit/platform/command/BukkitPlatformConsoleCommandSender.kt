package io.github.theramu.servershout.bukkit.platform.command

import io.github.theramu.servershout.common.platform.command.PlatformConsoleCommandSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * @author TheRamU
 * @since 2024/8/27 1:23
 */
class BukkitPlatformConsoleCommandSender(
    override val handle: CommandSender
) : PlatformConsoleCommandSender {

    override fun sendMessage(message: String) {
        handle.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    override fun sendMessage(component: Component) {
        handle.sendMessage(LegacyComponentSerializer.legacySection().serialize(component))
    }

    override fun hasPermission(permission: String) = handle.hasPermission(permission)
}