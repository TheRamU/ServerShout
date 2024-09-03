package io.github.theramu.servershout.common.token

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.exception.ServiceException

/**
 * @author TheRamU
 * @since 2024/8/27 4:26
 */
class TokenService {

    private val api get() = ServerShoutApi.api
    private val mySqlAccessor get() = api.mySqlAccessor
    private val databaseSettings get() = api.configLoader.databaseSettings
    private val logger get() = api.logger
    private val cache = mutableMapOf<String, Token>()

    fun getToken(name: String): Token? {
        return cache[name]
    }

    fun getTokenList(check: Boolean = true): List<Token> {
        if (check) {
            checkDatabase()
            updateCache()
        }
        return cache.values.toList()
    }

    fun createToken(name: String) {
        checkDatabase()
        val id = try {
            mySqlAccessor.insertToken(name)
        } catch (e: Exception) {
            logger.error("Failed to create token", e)
            throw ServiceException(50002, language = "message.commands.token-create.failed", args = arrayOf(name))
        }
        val token = Token(id, name)
        cache[name] = token
    }

    fun deleteToken(name: String) {
        checkDatabase()
        try {
            mySqlAccessor.deleteToken(cache[name]!!.id)
        } catch (e: Exception) {
            logger.error("Failed to delete token", e)
            throw ServiceException(50002, language = "message.commands.token-delete.failed", args = arrayOf(name))
        }
        cache.remove(name)
    }

    fun updateCache() {
        cache.clear()
        if (!databaseSettings.enabled) return
        try {
            cache.putAll(mySqlAccessor.queryTokenList().associateBy { it.name })
        } catch (e: Exception) {
            logger.error("Failed to update token cache", e)
            throw ServiceException(50002, language = "message.plugin.database-error")
        }
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