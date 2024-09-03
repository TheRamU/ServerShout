package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/25 8:08
 */
class TokenCreateCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val tokenService get() = api.tokenService
    override val arguments = arrayOf("token", "create", "<NAME>")
    override val permission = "servershout.token.create"
    override val playerOnly = false
    override val usage get() = "message.commands.token-create.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val tokenName = args.getOrNull(2)!!
        if (!tokenName.matches(Regex("^[A-Z0-9_]+$"))) {
            sender.sendLanguageMessage("message.commands.token-create.invalid-name")
            return
        }
        if (tokenName.length > 16) {
            sender.sendLanguageMessage("message.commands.token-create.name-too-long")
            return
        }
        if (tokenService.getToken(tokenName) != null) {
            sender.sendLanguageMessage("message.commands.token-create.already-exists", tokenName)
            return
        }
        tokenService.createToken(tokenName)
        sender.sendLanguageMessage("message.commands.token-create.success", tokenName)
    }
}