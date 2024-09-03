package io.github.theramu.servershout.bungee.platform.logging

import io.github.theramu.servershout.bungee.ServerShoutBungeeApi
import io.github.theramu.servershout.bungee.ServerShoutBungeePlugin
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.logging.PlatformLogger
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import java.text.MessageFormat

/**
 * @author TheRamU
 * @since 2024/8/28 18:17
 */
class BungeePlatformLogger : PlatformLogger() {

    companion object {
        private const val PLUGIN_PREFIX = "[ServerShout] "
    }

    private val api get() = ServerShoutApi.api as ServerShoutBungeeApi
    private val logger get() = ServerShoutBungeePlugin.getInstance().logger

    @Suppress("DEPRECATION")
    override fun info(message: String?, vararg args: Any?) {
        val formatted = MessageFormat.format("$message", *args)
        ProxyServer.getInstance().console.sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN_PREFIX + formatted))
    }

    override fun error(message: String?, vararg args: Any?) {
        logger.severe(MessageFormat.format("$message", *args))
    }

    override fun warn(message: String?, vararg args: Any?) {
        logger.warning(MessageFormat.format("$message", *args))
    }

    @Suppress("DEPRECATION")
    override fun sendMessage(receiver: Any, message: String, vararg args: Any) {
        if (receiver is CommandSender) {
            val formatted = MessageFormat.format(message, *args)
            receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted))
        }
    }

    override fun sendLanguageMessage(receiver: Any, path: String, vararg args: Any) {
        if (receiver !is CommandSender) return
        val configLoader = api.configLoader
        val language = configLoader.language(path)
        sendMessage(receiver, configLoader.messagePrefix + language, *args)
    }
}