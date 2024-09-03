package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/25 4:03
 */
class HelpCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    override val arguments = arrayOf("help")
    override val permission = "servershout.help"
    override val playerOnly = false
    override val usage get() = "message.commands.help.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        sender.sendMessage("")
        sender.sendMessage(" §e§lServerShout §f§lv${api.version} §r§7by TheRamU")
        sender.sendMessage("")
        val handlers = api.commandManager.getHandlerList().filter {
            it.isAllowedSender(sender) && it.hasPermission(sender) && !it.hidden && it.usage != null
        }
        handlers.forEach { handler ->
            val usageLanguage = api.configLoader.language(handler.usage!!).replace("{0}", alias)
            sender.sendMessage(" $usageLanguage")
        }
        if (handlers.isNotEmpty()) sender.sendMessage("")
    }
}