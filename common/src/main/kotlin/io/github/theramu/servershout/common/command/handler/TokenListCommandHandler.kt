package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/27 6:00
 */
class TokenListCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    override val arguments = arrayOf("token", "list")
    override val permission = "servershout.token.list"
    override val playerOnly = false
    override val usage get() = "message.commands.token-list.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val tokenList = api.tokenService.getTokenList()
        sender.sendLanguageMessage("message.commands.token-list.list-header")
        sender.sendMessage("")
        tokenList.forEach({ token ->
            sender.sendMessage(" §7- §f${token.name}")
        })
        sender.sendMessage("")
    }
}