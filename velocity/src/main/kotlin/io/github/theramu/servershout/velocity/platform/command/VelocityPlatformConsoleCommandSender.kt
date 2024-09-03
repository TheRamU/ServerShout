package io.github.theramu.servershout.velocity.platform.command

import com.velocitypowered.api.command.CommandSource
import io.github.theramu.servershout.common.platform.command.PlatformConsoleCommandSender
import io.github.theramu.servershout.common.util.ColorUtil
import net.kyori.adventure.text.Component

/**
 * @author TheRamU
 * @since 2024/8/27 1:20
 */
class VelocityPlatformConsoleCommandSender(
    override val handle: CommandSource
) : PlatformConsoleCommandSender {

    override fun sendMessage(message: String) {
        handle.sendMessage(ColorUtil.deserializeComponent(message))
    }

    override fun sendMessage(component: Component) {
        handle.sendMessage(component)
    }

    override fun hasPermission(permission: String) = handle.hasPermission(permission)
}