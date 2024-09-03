package io.github.theramu.servershout.bukkit.platform.plugin

import io.github.theramu.servershout.common.platform.plugin.PlatformPluginManager

/**
 * @author TheRamU
 * @since 2024/9/2 18:35
 */
class BukkitPlatformPluginManager : PlatformPluginManager {
    override fun isPluginEnabled(pluginName: String): Boolean {
        return org.bukkit.Bukkit.getPluginManager().isPluginEnabled(pluginName)
    }
}