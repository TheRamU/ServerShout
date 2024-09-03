package io.github.theramu.servershout.common.balance

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.cache.Cache
import io.github.theramu.servershout.common.exception.ServiceException
import io.github.theramu.servershout.common.token.Token
import io.github.theramu.servershout.common.util.UuidUtil
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 15:03
 */
class BalanceService {
    private val api get() = ServerShoutApi.api
    private val mySqlAccessor get() = api.mySqlAccessor
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val cache = Cache<UUID, MutableMap<Token, Balance?>>()

    fun getBalance(playerName: String, token: Token): Balance? {
        return getBalance(UuidUtil.toUuid(playerName), token)
    }

    fun getBalance(uuid: UUID, token: Token): Balance? {
        checkDatabase()
        cache[uuid]?.get(token)?.let { return it }
        val balance = mySqlAccessor.queryBalance(uuid, token)
        cache.getOrPut(uuid) { mutableMapOf() }[token] = balance
        return balance
    }

    fun getBalanceAmount(playerName: String, token: Token): Long {
        return getBalance(playerName, token)?.amount ?: 0
    }

    fun setBalanceAmount(playerName: String, token: Token, amount: Long) {
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        mySqlAccessor.updateBalance(uuid, token, amount)
        cache.getOrPut(uuid) { mutableMapOf() }[token] = Balance(-1, uuid, token, amount)
    }

    fun giveBalance(playerName: String, token: Token, amount: Long) {
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        val balanceAmount = getBalanceAmount(playerName, token)
        mySqlAccessor.giveBalance(uuid, token, amount)
        cache.getOrPut(uuid) { mutableMapOf() }[token] = Balance(-1, uuid, token, balanceAmount + amount)
    }

    fun takeBalance(playerName: String, token: Token, amount: Long) {
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        val balance = getBalance(uuid, token)
        if (balance == null || balance.amount < amount || !mySqlAccessor.takeBalance(uuid, token, amount)) {
            throw ServiceException(40002, language = "message.commands.balance-take.not-enough", args = arrayOf(playerName, token.name))
        }
        balance.amount -= amount
    }

    fun hasBalance(playerName: String, token: Token, amount: Long): Boolean {
        return getBalanceAmount(playerName, token) >= amount
    }

    fun clearCache() {
        cache.clear()
    }

    fun removeCache(uuid: UUID) {
        cache.remove(uuid)
    }

    private fun checkDatabase() {
        if (!databaseSettings.enabled) {
            throw ServiceException(40001, language = "message.plugin.database-disabled")
        }
        if (mySqlAccessor.isClosed()) {
            throw ServiceException(50001, language = "message.plugin.database-error")
        }
    }
}