package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/28 15:45
 */
class BalanceSetCommandHandler : CommandHandler {
    private val api get() = ServerShoutApi.api
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val platform get() = api.platform
    private val balanceService get() = api.balanceService
    private val tokenService get() = api.tokenService
    override val arguments = arrayOf("balance", "set", "<NAME>", "<TOKEN>", "<AMOUNT>")
    override val permission = "servershout.balance.set"
    override val playerOnly = false
    override val usage get() = "message.commands.balance-set.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        if (!databaseSettings.enabled) {
            sender.sendLanguageMessage("message.plugin.database-disabled")
            return
        }
        val playerName = args[2]
        val tokenName = args[3]
        val amount = args[4].toLongOrNull()?.takeIf { it > 0 } ?: run {
            sender.sendLanguageMessage("message.command.invalid-amount")
            return
        }
        val token = tokenService.getToken(tokenName) ?: run {
            sender.sendLanguageMessage("message.command.token-not-found", tokenName)
            return
        }
        balanceService.setBalanceAmount(playerName, token, amount)
        sender.sendLanguageMessage("message.commands.balance-set.success", playerName, tokenName, amount)
        api.sendUpdate(playerName)
    }

    override fun variableSuggest(sender: PlatformCommandSender, args: Array<String>, variable: String): List<String> {
        return when (variable) {
            "<NAME>" -> platform.onlinePlayers.map { it.name }
            "<TOKEN>" -> api.tokenService.getTokenList(false).map { it.name }
            else -> emptyList()
        }
    }
}