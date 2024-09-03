package io.github.theramu.servershout.bungee.platform.plugin

import io.github.theramu.servershout.common.platform.plugin.PlatformPluginManager
import net.md_5.bungee.api.ProxyServer

/**
 * @author TheRamU
 * @since 2024/9/2 18:37
 */
class BungeePlatformPluginManager : PlatformPluginManager {
    override fun isPluginEnabled(pluginName: String): Boolean {
        return ProxyServer.getInstance().pluginManager.getPlugin(pluginName) != null
    }
}