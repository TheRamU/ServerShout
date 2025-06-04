package io.github.theramu.servershout.common.balance

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.cache.Cache
import io.github.theramu.servershout.common.exception.ServiceException
import io.github.theramu.servershout.common.token.Token
import io.github.theramu.servershout.common.util.UuidUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * @author TheRamU
 * @since 2024/8/28 15:03
 */
class BalanceService {
    private val api get() = ServerShoutApi.api
    private val mySqlAccessor get() = api.mySqlAccessor
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val cache = Cache<UUID, ConcurrentHashMap<String, Balance>>(expiration = 30 * 60 * 1000)
    private val cacheLock = ReentrantReadWriteLock()

    fun getBalance(playerName: String, token: Token, force: Boolean = false): Balance? {
        return getBalance(UuidUtil.toUuid(playerName), token, force)
    }

    fun getBalance(uuid: UUID, token: Token, force: Boolean = false): Balance? {
        checkDatabase()
        if (!force) {
            cacheLock.read {
                cache[uuid]?.get(token.name)?.let { return if (it.id != -1) it else null }
            }
        }
        // 缓存未命中，从数据库加载并更新缓存
        val balance = mySqlAccessor.queryBalance(uuid, token)
        cacheLock.write {
            cache.getOrPut(uuid) { ConcurrentHashMap() }[token.name] = balance ?: Balance(-1, uuid, token, 0)
        }
        return balance
    }

    fun getBalanceAmount(playerName: String, token: Token, force: Boolean = false): Long {
        return getBalance(playerName, token, force)?.amount ?: 0
    }

    fun getBalanceAmount(uuid: UUID, token: Token, force: Boolean = false): Long {
        return getBalance(uuid, token, force)?.amount ?: 0
    }

    fun setBalanceAmount(playerName: String, token: Token, amount: Long) {
        require(amount >= 0) { "Balance cannot be negative" }
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        cacheLock.write {
            mySqlAccessor.updateBalance(uuid, token, amount)
            this.removeCache(uuid, token)
        }
    }

    fun giveBalance(playerName: String, token: Token, amount: Long) {
        require(amount >= 0) { "Balance cannot be negative" }
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        cacheLock.write {
            mySqlAccessor.giveBalance(uuid, token, amount)
            this.removeCache(uuid, token)
        }
    }

    fun takeBalance(playerName: String, token: Token, amount: Long) {
        require(amount >= 0) { "Balance cannot be negative" }
        checkDatabase()
        val uuid = UuidUtil.toUuid(playerName)
        cacheLock.write {
            val balance = getBalance(uuid, token, force = true)
            if (balance == null || balance.amount < amount || !mySqlAccessor.takeBalance(uuid, token, amount)) {
                throw ServiceException(40002, language = "message.commands.balance-take.not-enough", args = arrayOf(playerName, token.name))
            }
            this.removeCache(uuid, token)
        }
    }

    fun hasBalance(playerName: String, token: Token, amount: Long, force: Boolean = false): Boolean {
        return getBalanceAmount(playerName, token, force) >= amount
    }

    fun hasBalance(uuid: UUID, token: Token, amount: Long, force: Boolean = false): Boolean {
        return getBalanceAmount(uuid, token, force) >= amount
    }

    fun clearCache() {
        cacheLock.write { cache.clear() }
    }

    fun removeCache(uuid: UUID) {
        cacheLock.write { cache.remove(uuid) }
    }

    fun removeCache(uuid: UUID, token: Token) {
        cacheLock.write { cache[uuid]?.remove(token.name) }
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