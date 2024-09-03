package io.github.theramu.servershout.bukkit

import io.github.theramu.servershout.bukkit.command.BukkitCommandAdapter
import io.github.theramu.servershout.bukkit.hook.ServerShoutPlaceholderExpansion
import io.github.theramu.servershout.bukkit.listener.PlayerEventListener
import io.github.theramu.servershout.bukkit.listener.PluginChannelMessageListener
import io.github.theramu.servershout.bukkit.platform.BukkitPlatform
import io.github.theramu.servershout.bukkit.platform.logging.BukkitPlatformLogger
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.util.UuidUtil
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * @author TheRamU
 * @since 2024/08/18 23:45
 */
open class ServerShoutBukkitApi protected constructor(
    private val plugin: ServerShoutBukkitPlugin
) : ServerShoutApi() {

    override val platform = BukkitPlatform()
    override val logger = BukkitPlatformLogger()
    override val dataFolder = plugin.dataFolder
    private var metrics: Metrics? = null

    override fun onEnable() {
        super.onEnable()
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "servershout:main")
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "servershout:main", PluginChannelMessageListener());
        Bukkit.getPluginManager().registerEvents(PlayerEventListener(), plugin)
        registerCommands()
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ServerShoutPlaceholderExpansion().register()
        }
        metrics = Metrics(plugin, 23117)
        logger.info("&aServerShout is ready!")
    }

    override fun onDisable() {
        super.onDisable()
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ServerShoutPlaceholderExpansion().unregister()
        }
        metrics?.shutdown()
    }

    private fun registerCommands() {
        val commandAdapter = BukkitCommandAdapter()
        with(Bukkit.getPluginCommand("servershout")) {
            executor = commandAdapter
            tabCompleter = commandAdapter
        }
    }

    override fun sendUpdate(playerName: String) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return
        val medium = Bukkit.getOnlinePlayers().toList().get(0)
        ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { out ->
                out.writeUTF("UPDATE_BALANCE")
                out.writeUTF(UuidUtil.toUuid(playerName).toString())
            }
            medium.sendPluginMessage(plugin, "servershout:main", bytes.toByteArray())
        }
    }
}