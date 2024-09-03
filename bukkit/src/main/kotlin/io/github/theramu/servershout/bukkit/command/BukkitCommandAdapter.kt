package io.github.theramu.servershout.bukkit.command

import io.github.theramu.servershout.bukkit.platform.command.BukkitPlatformConsoleCommandSender
import io.github.theramu.servershout.bukkit.platform.player.BukkitPlatformPlayer
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * @author TheRamU
 * @since 2024/8/25 2:44
 */
class BukkitCommandAdapter : CommandExecutor, TabCompleter {

    private val api get() = ServerShoutApi.api
    private val platformCommand get() = api.commandManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        platformCommand.execute(sender.toPlatformCommandSender(), "ss", args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<String>): List<String> {
        return platformCommand.suggest(sender.toPlatformCommandSender(), "ss", args)
    }

    private fun CommandSender.toPlatformCommandSender(): PlatformCommandSender {
        return if (this is Player) {
            BukkitPlatformPlayer(this)
        } else {
            BukkitPlatformConsoleCommandSender(this)
        }
    }
}