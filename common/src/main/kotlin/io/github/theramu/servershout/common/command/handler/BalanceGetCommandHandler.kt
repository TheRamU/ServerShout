package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/28 13:40
 */
class BalanceGetCommandHandler : CommandHandler {
    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val platform get() = api.platform
    private val balanceService get() = api.balanceService
    private val tokenService get() = api.tokenService
    override val arguments = arrayOf("balance", "get", "<NAME>", "<TOKEN>")
    override val permission = "servershout.balance.get"
    override val playerOnly = false
    override val usage get() = "message.commands.balance-get.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val playerName = args[2]
        val tokenName = args[3]
        val token = tokenService.getToken(tokenName) ?: run {
            sender.sendLanguageMessage("message.command.token-not-found", tokenName)
            return
        }
        val balanceAmount = balanceService.getBalanceAmount(playerName, token)
        sender.sendLanguageMessage("message.commands.balance-get.success", playerName, tokenName, balanceAmount)
    }

    override fun variableSuggest(sender: PlatformCommandSender, args: Array<String>, variable: String): List<String> {
        return when (variable) {
            "<NAME>" -> platform.onlinePlayers.map { it.name }
            "<TOKEN>" -> api.tokenService.getTokenList(false).map { it.name }
            else -> emptyList()
        }
    }
}