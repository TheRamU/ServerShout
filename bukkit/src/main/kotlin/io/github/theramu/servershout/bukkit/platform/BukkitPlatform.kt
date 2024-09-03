package io.github.theramu.servershout.bukkit.platform

import io.github.theramu.servershout.bukkit.platform.command.BukkitPlatformConsoleCommandSender
import io.github.theramu.servershout.bukkit.platform.player.BukkitPlatformPlayer
import io.github.theramu.servershout.bukkit.platform.plugin.BukkitPlatformPluginManager
import io.github.theramu.servershout.bukkit.platform.scheduler.BukkitPlatformScheduler
import io.github.theramu.servershout.common.platform.Platform
import org.bukkit.Bukkit

/**
 * @author TheRamU
 * @since 2024/9/1 0:52
 */
class BukkitPlatform : Platform {
    override val onlinePlayers get() = Bukkit.getOnlinePlayers().map { BukkitPlatformPlayer(it) }
    override val scheduler = BukkitPlatformScheduler()
    override val consoleCommandSender = BukkitPlatformConsoleCommandSender(Bukkit.getConsoleSender())
    override val pluginManager = BukkitPlatformPluginManager()
}