package io.github.theramu.servershout.common.config

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.config.settings.DatabaseSettings
import io.github.theramu.servershout.common.config.settings.ShoutGlobalSettings
import io.github.theramu.servershout.common.config.settings.UpdateCheckSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author TheRamU
 * @since 2024/08/20 04:12
 */
class ConfigLoader(
    private val dataFolder: File
) {
    companion object {
        private val VERIFY_RULES = mapOf(
            "config.yml" to listOf(listOf(), listOf()),
            "language.yml" to listOf(listOf(), listOf()),
            "shout.yml" to listOf(
                listOf("global.server-map.{key}", "shouts.{key}"),
                listOf()
            )
        )
    }

    private val api get() = ServerShoutApi.api
    private val logger get() = api.logger
    private val localeLoader = LocaleLoader()
    lateinit var pluginConfig: YamlConfig private set
    lateinit var shoutConfig: YamlConfig private set
    lateinit var languageConfig: YamlConfig private set
    lateinit var databaseSettings: DatabaseSettings private set
    lateinit var updateCheckSettings: UpdateCheckSettings private set
    lateinit var shoutGlobalSettings: ShoutGlobalSettings private set
    lateinit var messagePrefix: String private set

    fun loadConfig(isProxy: Boolean) {
        logger.info("Loading configuration...")
        localeLoader.loadLocale()
        logger.info("Loading plugin configuration...")
        pluginConfig = getVerifiedConfig("config.yml")
        databaseSettings = pluginConfig.get("database", DatabaseSettings::class.java)
        updateCheckSettings = pluginConfig.get("update-check", UpdateCheckSettings::class.java)
        if (isProxy) {
            logger.info("Loading shout configuration...")
            shoutConfig = getVerifiedConfig("shout.yml")
            shoutGlobalSettings = shoutConfig.get("global", ShoutGlobalSettings::class.java)
        }
        logger.info("Loading language configuration...")
        languageConfig = getVerifiedConfig("language.yml")
        messagePrefix = language("message.prefix")
    }

    private fun getVerifiedConfig(fileName: String): YamlConfig {
        val file = File(dataFolder, fileName)
        if (!file.exists()) {
            localeLoader.saveLocaleResource(fileName)
            return YamlConfig(file)
        }

        val oldFileName = "${fileName.split(".")[0]}-${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())}-old.yml"
        val oldFile = File(dataFolder, oldFileName)

        // 尝试加载配置文件
        var config = try {
            YamlConfig(file)
        } catch (e: Exception) {
            logger.warn("[$fileName] configuration is damaged, trying to repair...")
            handleConfigError(fileName, file, oldFile)
            return YamlConfig(file)
        }

        // 校验配置文件
        val configMap = config.getMap()
        val templateConfig = YamlConfig(localeLoader.getLocaleResource(fileName)!!)
        val fixCode = verifyConfig(fileName, configMap, templateConfig.getMap(), "")

        if (fixCode == 1) {
            return config
        }

        // 备份损坏的配置文件，保存修复后的配置文件
        file.copyTo(oldFile)
        logger.warn("[$fileName] configuration is damaged, trying to repair...")
        configMap["config-version"] = templateConfig.get("config-version")
        config.update(configMap)
        config.save(file, templateConfig.getLines())

        // 重新加载修复后的配置文件
        config = try {
            YamlConfig(file)
        } catch (e: Exception) {
            handleConfigError(fileName, file, oldFile)
            return YamlConfig(file)
        }

        // 再次校验配置文件
        if (verifyConfig(fileName, config.getMap(), templateConfig.getMap(), "", false) != 1) {
            handleConfigError(fileName, file, oldFile)
            return YamlConfig(file)
        }

        logger.info("&a[$fileName] configuration repaired successfully!")
        return config
    }

    private fun handleConfigError(fileName: String, file: File, oldFile: File) {
        file.renameTo(oldFile)
        localeLoader.saveLocaleResource(fileName)
        logger.error("[$fileName] configuration repair failed, loaded as default configuration!")
    }

    private fun verifyConfig(
        fileName: String,
        configMap: MutableMap<String, Any>,
        templateMap: Map<String, Any>,
        path: String = "",
        log: Boolean = true
    ): Int {
        val (keyPathList, ignorePathList) = VERIFY_RULES[fileName]!!
        var code = 1

        // 删除 configMap 中不存在于 templateMap 中的节点
        configMap.keys.subtract(templateMap.keys).forEach { key ->
            val currentPath = buildPath(path, key)
            if (!matchesPath(currentPath, keyPathList)) {
                if (log) logger.warn("[$fileName] unknown node -> $currentPath")
                configMap.remove(key)
                code = 2.coerceAtLeast(code)
            }
        }

        for ((templateKey, templateValue) in templateMap) {
            // 如果当前路径是可变的，遍历 configMap 中的所有键
            val keys = if (matchesPath(buildPath(path, templateKey), keyPathList)) configMap.keys else setOf(templateKey)

            for (configKey in keys) {
                val configValue = configMap[configKey]
                val currentConfigPath = buildPath(path, configKey)

                when {
                    configValue == null && !matchesPath(currentConfigPath, ignorePathList) -> {
                        if (log) logger.warn("[$fileName] missing node -> $currentConfigPath")
                        configMap[configKey] = templateValue
                        code = 2.coerceAtLeast(code)
                    }

                    configValue is Map<*, *> && templateValue is Map<*, *> -> {
                        val mutableConfigValue = configValue.toMutableMap()
                        configMap[configKey] = mutableConfigValue
                        @Suppress("UNCHECKED_CAST")
                        code = verifyConfig(
                            fileName,
                            mutableConfigValue as MutableMap<String, Any>,
                            templateValue as Map<String, Any>,
                            currentConfigPath
                        ).coerceAtLeast(code)
                    }

                    configValue != null && configValue::class != templateValue::class -> {
                        if (log) logger.warn("[$fileName] illegal node data type ${configValue::class.simpleName} should be ${templateValue::class.simpleName} -> $currentConfigPath")
                        configMap[configKey] = templateValue
                        code = 3.coerceAtLeast(code)
                    }
                }
            }
        }
        return code
    }

    private fun buildPath(path: String, key: String): String {
        return if (path.isEmpty()) key else "$path.$key"
    }

    private fun matchesPath(path: String, pathList: List<String>): Boolean {
        for (pattern in pathList) {
            val regex = pattern.replace("{key}", "[^.]+").replace(".", "\\.").toRegex()
            if (regex.matches(path)) {
                return true
            }
        }
        return false
    }

    fun language(path: String): String {
        return languageConfig.getString(path, "MISSING: $path")
    }
}