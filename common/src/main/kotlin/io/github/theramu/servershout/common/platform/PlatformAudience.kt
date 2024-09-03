package io.github.theramu.servershout.common.platform

import io.github.theramu.servershout.common.ServerShoutApi
import net.kyori.adventure.text.Component

/**
 * @author TheRamU
 * @since 2024/8/27 2:01
 */
interface PlatformAudience {
    val handle: Any
    fun sendMessage(message: String)
    fun sendMessage(component: Component)
    fun sendLanguageMessage(language: String, vararg args: Any) {
        ServerShoutApi.api.logger.sendLanguageMessage(handle, language, *args)
    }
}