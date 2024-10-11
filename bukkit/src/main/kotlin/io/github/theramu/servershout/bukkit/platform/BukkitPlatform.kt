package io.github.theramu.servershout.bukkit.platform

import io.github.theramu.servershout.bukkit.ServerShoutBukkitPlugin
import io.github.theramu.servershout.bukkit.platform.command.BukkitPlatformConsoleCommandSender
import io.github.theramu.servershout.bukkit.platform.player.BukkitPlatformPlayer
import io.github.theramu.servershout.bukkit.platform.plugin.BukkitPlatformPluginManager
import io.github.theramu.servershout.bukkit.platform.scheduler.BukkitPlatformScheduler
import io.github.theramu.servershout.common.platform.Platform
import io.github.theramu.servershout.folia.platform.scheduler.FoliaPlatformScheduler
import org.bukkit.Bukkit

/**
 * @author TheRamU
 * @since 2024/9/1 0:52
 */
class BukkitPlatform : Platform {
    private val plugin = ServerShoutBukkitPlugin.getInstance()
    override val onlinePlayers get() = Bukkit.getOnlinePlayers().map { BukkitPlatformPlayer(it) }
    override val scheduler = if (isFolia()) FoliaPlatformScheduler(plugin) else BukkitPlatformScheduler()
    override val consoleCommandSender = BukkitPlatformConsoleCommandSender(Bukkit.getConsoleSender())
    override val pluginManager = BukkitPlatformPluginManager()

    private fun isFolia(): Boolean = try {
        Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}