package io.github.theramu.servershout.bukkit.hook

import io.github.theramu.servershout.common.ServerShoutApi
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

/**
 * @author TheRamU
 * @since 2024/8/29 1:37
 */
class ServerShoutPlaceholderExpansion : PlaceholderExpansion() {

    private val api get() = ServerShoutApi.api
    private val tokenService get() = api.tokenService
    private val balanceService get() = api.balanceService

    override fun getIdentifier() = "servershout"

    override fun getAuthor() = "TheRamU"

    override fun getVersion() = api.version

    override fun onPlaceholderRequest(player: Player, arg: String): String {
        if (!arg.startsWith("balance_")) return "INVALID_PLACEHOLDER"
        val tokenName = arg.substring(8)
        val token = tokenService.getToken(tokenName) ?: return "INVALID_TOKEN"
        return balanceService.getBalance(player.uniqueId, token)?.amount?.toString() ?: "0"
    }
}