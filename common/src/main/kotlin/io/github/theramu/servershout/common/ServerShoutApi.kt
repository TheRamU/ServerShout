package io.github.theramu.servershout.common

import io.github.theramu.servershout.common.balance.BalanceService
import io.github.theramu.servershout.common.command.CommandManager
import io.github.theramu.servershout.common.config.ConfigLoader
import io.github.theramu.servershout.common.database.MySqlAccessor
import io.github.theramu.servershout.common.network.UpdateChecker
import io.github.theramu.servershout.common.platform.Platform
import io.github.theramu.servershout.common.platform.ProxyPlatform
import io.github.theramu.servershout.common.platform.logging.PlatformLogger
import io.github.theramu.servershout.common.token.TokenService
import java.io.File

/**
 * @author TheRamU
 * @since 2024/8/25 7:17
 */
abstract class ServerShoutApi protected constructor() {

    val version = BuildConstants.VERSION
    abstract val platform: Platform
    abstract val logger: PlatformLogger
    abstract val dataFolder: File
    val configLoader = ConfigLoader()
    val mySqlAccessor = MySqlAccessor()
    val tokenService = TokenService()
    val balanceService = BalanceService()
    val commandManager = CommandManager()
    val updateChecker = UpdateChecker()

    open fun onEnable() {
        logger.info("&7")
        logger.info("&7 &eServerShout &fv${version} &7by &lTheRamU")
        logger.info("&7")
        load()
        updateChecker.startTimer()
    }

    open fun onDisable() {
        mySqlAccessor.close()
        updateChecker.stopTimer()
    }

    open fun load() {
        configLoader.loadConfig(platform is ProxyPlatform)
        try {
            mySqlAccessor.connect()
        } catch (e: Exception) {
            configLoader.databaseSettings.enabled = false
            logger.error("Failed to connect to MySQL database", e)
            logger.warn("Database support has been disabled!")
        }
        tokenService.updateCache()
        balanceService.clearCache()
    }

    abstract fun sendUpdate(playerName: String)

    companion object {
        private lateinit var instance: ServerShoutApi

        @JvmStatic
        val api get() = instance
    }

    init {
        instance = this
    }
}