package io.github.theramu.servershout.velocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender
import io.github.theramu.servershout.velocity.platform.command.VelocityPlatformConsoleCommandSender
import io.github.theramu.servershout.velocity.platform.player.VelocityPlatformPlayer

/**
 * @author TheRamU
 * @since 2024/8/25 2:16
 */
class VelocityCommandAdapter : SimpleCommand {

    private val api get() = ServerShoutApi.api
    private val platformCommand get() = api.commandManager

    override fun execute(invocation: SimpleCommand.Invocation) {
        platformCommand.execute(invocation.sender(), "ssv", invocation.arguments())
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        var arguments = invocation.arguments()
        if (arguments.isEmpty()) {
            // 与其他平台保持一致
            arguments = arrayOf("")
        }
        return platformCommand.suggest(invocation.sender(), "ssv", arguments)
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return super.hasPermission(invocation)
    }

    private fun SimpleCommand.Invocation.sender(): PlatformCommandSender {
        val source = source()
        return if (source is Player) {
            VelocityPlatformPlayer(source)
        } else {
            VelocityPlatformConsoleCommandSender(source)
        }
    }
}