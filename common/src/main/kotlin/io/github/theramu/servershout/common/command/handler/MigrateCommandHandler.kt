package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/30 3:15
 */
class MigrateCommandHandler : CommandHandler {
    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val tokenService get() = api.tokenService
    private val mySqlAccessor get() = api.mySqlAccessor
    override val arguments = arrayOf("migrate", "<TOKEN>", "<_NULLABLE>")
    override val permission = "servershout.migrate"
    override val playerOnly = false
    override val usage get() = null
    override val hidden get() = true

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val tokenName = args.getOrNull(1)!!
        if (!"confirm".equals(args.getOrNull(2), ignoreCase = true)) {
            sender.sendLanguageMessage("message.commands.migrate.confirm", alias, tokenName)
            return
        }
        val token = tokenService.getToken(tokenName) ?: run {
            sender.sendLanguageMessage("message.command.token-not-found", tokenName)
            return
        }
        val affected = mySqlAccessor.migrate(token)
        if (affected == -1) {
            sender.sendLanguageMessage("message.commands.migrate.table-not-found")
            return
        }
        sender.sendLanguageMessage("message.commands.migrate.success", affected)
    }

    override fun variableSuggest(sender: PlatformCommandSender, args: Array<String>, variable: String): List<String> {
        return if (variable == "<TOKEN>") {
            api.tokenService.getTokenList(false).map { it.name }
        } else {
            emptyList()
        }
    }
}