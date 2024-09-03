package io.github.theramu.servershout.velocity.platform.plugin

import io.github.theramu.servershout.common.platform.plugin.PlatformPluginManager
import io.github.theramu.servershout.velocity.ServerShoutVelocityPlugin

/**
 * @author TheRamU
 * @since 2024/9/2 18:39
 */
class VelocityPlatformPluginManager : PlatformPluginManager {
    override fun isPluginEnabled(pluginName: String): Boolean {
        return ServerShoutVelocityPlugin.getProxy().pluginManager.isLoaded(pluginName)
    }
}