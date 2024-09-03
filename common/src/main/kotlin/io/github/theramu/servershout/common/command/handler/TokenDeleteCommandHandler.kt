package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/27 5:45
 */
class TokenDeleteCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val tokenService get() = api.tokenService
    override val arguments = arrayOf("token", "delete", "<NAME>", "<_NULLABLE>")
    override val permission = "servershout.token.delete"
    override val playerOnly = false
    override val usage get() = "message.commands.token-delete.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val tokenName = args.getOrNull(2)!!
        if (!"confirm".equals(args.getOrNull(3), ignoreCase = true)) {
            sender.sendLanguageMessage("message.commands.token-delete.confirm", alias, tokenName)
            return
        }
        if (tokenService.getToken(tokenName) == null) {
            sender.sendLanguageMessage("message.command.token-not-found", tokenName)
            return
        }
        tokenService.deleteToken(tokenName)
        sender.sendLanguageMessage("message.commands.token-delete.success", tokenName)
    }

    override fun variableSuggest(sender: PlatformCommandSender, args: Array<String>, variable: String): List<String> {
        return if (variable == "<NAME>") {
            api.tokenService.getTokenList(false).map { it.name }
        } else {
            emptyList()
        }
    }
}