package io.github.theramu.servershout.bungee.command

import io.github.theramu.servershout.bungee.platform.command.BungeePlatformConsoleCommandSender
import io.github.theramu.servershout.bungee.platform.player.BungeePlatformPlayer
import io.github.theramu.servershout.common.ServerShoutApi
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

/**
 * @author TheRamU
 * @since 2024/8/28 18:37
 */
class BungeeCommandAdapter : Command("servershoutbungee", null, "ssb") {

    private val api get() = ServerShoutApi.api
    private val platformCommand get() = api.commandManager

    override fun execute(sender: CommandSender, args: Array<String>) {
        val bungeeSender = if (sender is ProxiedPlayer) {
            BungeePlatformPlayer(sender)
        } else {
            BungeePlatformConsoleCommandSender(sender)
        }
        platformCommand.execute(bungeeSender, "ssb", args)
    }
}