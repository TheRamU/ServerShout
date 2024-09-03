package io.github.theramu.servershout.velocity.platform.logging

import com.velocitypowered.api.command.CommandSource
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.logging.PlatformLogger
import io.github.theramu.servershout.common.util.ColorUtil
import io.github.theramu.servershout.velocity.ServerShoutVelocityApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.text.MessageFormat


/**
 * @author TheRamU
 * @since 2024/8/20 23:48
 */
class VelocityPlatformLogger : PlatformLogger() {

    private val api get() = ServerShoutApi.api as ServerShoutVelocityApi
    private var logger = ComponentLogger.logger("servershout")

    override fun info(message: String?, vararg args: Any?) {
        logger.info(deserializeComponent(message, *args))
    }

    override fun error(message: String?, vararg args: Any?) {
        logger.error(deserializeComponent(message, *args))
    }

    override fun warn(message: String?, vararg args: Any?) {
        logger.warn(deserializeComponent(message, *args))
    }

    override fun sendMessage(receiver: Any, message: String, vararg args: Any) {
        if (receiver !is CommandSource) throw IllegalArgumentException("receiver is not a CommandSource")
        receiver.sendMessage(deserializeComponent(message, *args))
    }

    override fun sendLanguageMessage(receiver: Any, path: String, vararg args: Any) {
        if (receiver !is CommandSource) throw IllegalArgumentException("receiver is not a CommandSource")
        val configLoader = api.configLoader
        val language = configLoader.language(path)
        sendMessage(receiver, configLoader.messagePrefix + language, *args)
    }

    private fun deserializeComponent(message: String?, vararg args: Any?): Component {
        return ColorUtil.deserializeComponent(MessageFormat.format("$message", *args))
    }
}