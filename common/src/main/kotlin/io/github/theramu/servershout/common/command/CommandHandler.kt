package io.github.theramu.servershout.common.command

import io.github.theramu.servershout.common.platform.command.PlatformCommandSender
import io.github.theramu.servershout.common.platform.player.PlatformPlayer

/**
 * @author TheRamU
 * @since 2024/8/25 3:20
 */
interface CommandHandler {

    val arguments: Array<String>

    val permission: String?

    val playerOnly: Boolean

    val usage: String?

    val hidden: Boolean get() = false

    fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>)

    fun variableSuggest(sender: PlatformCommandSender, args: Array<String>, variable: String): List<String> {
        return emptyList()
    }

    fun hasPermission(sender: PlatformCommandSender): Boolean {
        return permission == null || sender.hasPermission(permission!!)
    }

    fun isAllowedSender(sender: PlatformCommandSender): Boolean {
        return !playerOnly || sender is PlatformPlayer
    }
}