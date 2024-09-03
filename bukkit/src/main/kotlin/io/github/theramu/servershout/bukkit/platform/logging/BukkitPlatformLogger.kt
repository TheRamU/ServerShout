package io.github.theramu.servershout.bukkit.platform.logging

import io.github.theramu.servershout.bukkit.ServerShoutBukkitPlugin
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.logging.PlatformLogger
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.text.MessageFormat

/**
 * @author TheRamU
 * @since 2024/8/25 5:41
 */
class BukkitPlatformLogger : PlatformLogger() {

    companion object {
        private const val PLUGIN_PREFIX = "[ServerShout] "
    }

    private val api get() = ServerShoutApi.api
    private val logger get() = ServerShoutBukkitPlugin.getInstance().logger

    override fun info(message: String?, vararg args: Any?) {
        val formatted = MessageFormat.format("$message", *args)
        Bukkit.getServer().consoleSender.sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN_PREFIX + formatted))
    }

    override fun error(message: String?, vararg args: Any?) {
        logger.severe(MessageFormat.format("$message", *args))
    }

    override fun warn(message: String?, vararg args: Any?) {
        logger.warning(MessageFormat.format("$message", *args))
    }

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