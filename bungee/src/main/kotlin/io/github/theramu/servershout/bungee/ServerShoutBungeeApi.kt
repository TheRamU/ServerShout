package io.github.theramu.servershout.bungee

import io.github.theramu.servershout.bungee.command.BungeeCommandAdapter
import io.github.theramu.servershout.bungee.listener.PlayerEventListener
import io.github.theramu.servershout.bungee.listener.PluginChannelMessageListener
import io.github.theramu.servershout.bungee.platform.BungeePlatform
import io.github.theramu.servershout.bungee.platform.logging.BungeePlatformLogger
import io.github.theramu.servershout.common.ServerShoutProxyApi
import net.md_5.bungee.api.ProxyServer
import org.bstats.bungeecord.Metrics
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * @author TheRamU
 * @since 2024/8/28 18:16
 */
class ServerShoutBungeeApi(
    private val plugin: ServerShoutBungeePlugin
) : ServerShoutProxyApi() {

    override val platform = BungeePlatform()
    override val logger = BungeePlatformLogger()
    override val dataFolder = plugin.dataFolder
    private var metrics: Metrics? = null

    override fun onEnable() {
        super.onEnable()
        ProxyServer.getInstance().registerChannel("servershout:main")
        ProxyServer.getInstance().pluginManager.registerListener(plugin, PlayerEventListener())
        ProxyServer.getInstance().pluginManager.registerListener(plugin, PluginChannelMessageListener())
        registerCommands()
        metrics = Metrics(plugin, 6453)
        logger.info("&aServerShout is ready!")
    }

    override fun onDisable() {
        super.onDisable()
        ProxyServer.getInstance().pluginManager.unregisterListeners(plugin)
        ProxyServer.getInstance().pluginManager.unregisterCommands(plugin)
        metrics?.shutdown()
    }

    private fun registerCommands() {
        ProxyServer.getInstance().pluginManager.registerCommand(plugin, BungeeCommandAdapter())
    }

    override fun sendUpdate(playerName: String) {
        ProxyServer.getInstance().getPlayer(playerName)?.let { player ->
            ByteArrayOutputStream().use { bytes ->
                DataOutputStream(bytes).use { out ->
                    out.writeUTF("UPDATE_BALANCE")
                    out.writeUTF(player.uniqueId.toString())
                }
                player.server.sendData("servershout:main", bytes.toByteArray())
            }
        }
    }
}