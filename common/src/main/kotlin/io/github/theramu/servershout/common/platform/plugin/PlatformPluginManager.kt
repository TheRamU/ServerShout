package io.github.theramu.servershout.common.platform.plugin

/**
 * @author TheRamU
 * @since 2024/9/2 18:34
 */
interface PlatformPluginManager {
    fun isPluginEnabled(pluginName: String): Boolean
}