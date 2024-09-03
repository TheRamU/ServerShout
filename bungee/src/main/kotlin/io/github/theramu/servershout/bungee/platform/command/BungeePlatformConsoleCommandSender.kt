package io.github.theramu.servershout.bungee.platform.command

import io.github.theramu.servershout.common.platform.command.PlatformConsoleCommandSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender

/**
 * @author TheRamU
 * @since 2024/8/28 18:24
 */
@Suppress("DEPRECATION")
class BungeePlatformConsoleCommandSender(
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