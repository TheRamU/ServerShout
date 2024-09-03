package io.github.theramu.servershout.velocity

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import io.github.theramu.servershout.common.ServerShoutProxyApi
import io.github.theramu.servershout.velocity.command.VelocityCommandAdapter
import io.github.theramu.servershout.velocity.listener.PlayerEventListener
import io.github.theramu.servershout.velocity.listener.PluginChannelMessageListener
import io.github.theramu.servershout.velocity.platform.VelocityPlatform
import io.github.theramu.servershout.velocity.platform.logging.VelocityPlatformLogger
import org.bstats.velocity.Metrics
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.file.Path

/**
 * @author TheRamU
 * @since 2024/08/19 00:44
 */
open class ServerShoutVelocityApi protected constructor(
    private val plugin: ServerShoutVelocityPlugin,
    val proxy: ProxyServer,
    dataDirectory: Path
) : ServerShoutProxyApi() {
    override val platform = VelocityPlatform(proxy)
    override val logger = VelocityPlatformLogger()
    override val dataFolder = dataDirectory.toFile()
    val identifier = MinecraftChannelIdentifier.from("servershout:main")
    private var metrics: Metrics? = null

    override fun onEnable() {
        super.onEnable()
        proxy.channelRegistrar.register(identifier)
        proxy.eventManager.register(plugin, PlayerEventListener())
        proxy.eventManager.register(plugin, PluginChannelMessageListener())
        registerCommands()
        metrics = makeMetrics()
        logger.info("&aServerShout is ready!")
    }

    override fun onDisable() {
        super.onDisable()
        proxy.eventManager.unregisterListeners(plugin)
        metrics?.shutdown()
    }

    private fun registerCommands() {
        val proxyCommandManager = proxy.commandManager
        val commandMeta = proxyCommandManager.metaBuilder("servershoutvelocity")
            .aliases("ssv")
            .plugin(plugin)
            .build()
        proxyCommandManager.register(commandMeta, VelocityCommandAdapter())
    }

    private fun makeMetrics(): Metrics {
        val clazz = Metrics::class.java
        val constructor = clazz.getDeclaredConstructor(Any::class.java, ProxyServer::class.java, Logger::class.java, Path::class.java, Int::class.java)
        constructor.isAccessible = true
        return constructor.newInstance(plugin, proxy, logger, dataFolder.toPath(), 23116)
    }

    override fun sendUpdate(playerName: String) {
        val optional = proxy.getPlayer(playerName)
        if (!optional.isPresent) return
        optional?.get()?.let { player ->
            ByteArrayOutputStream().use { bytes ->
                DataOutputStream(bytes).use { out ->
                    out.writeUTF("UPDATE_BALANCE")
                    out.writeUTF(player.uniqueId.toString())
                }
                player.currentServer.get().sendPluginMessage(identifier, bytes.toByteArray())
            }
        }
    }
}
