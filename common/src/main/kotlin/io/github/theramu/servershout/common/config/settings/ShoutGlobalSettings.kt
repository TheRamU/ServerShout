package io.github.theramu.servershout.common.config.settings

import java.io.Serializable

/**
 *
 * @author TheRamU
 * @since 2024/9/1 15:09
 */
data class ShoutGlobalSettings(
    val logging: Boolean,
    val serverMap: Map<String, String>,
    val serverList: ServerListSettings
) : Serializable {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): ShoutGlobalSettings {
            @Suppress("UNCHECKED_CAST")
            return ShoutGlobalSettings(
                logging = map["logging"] as Boolean,
                serverMap = map["server-map"] as Map<String, String>,
                serverList = ServerListSettings.deserialize(map["server-list"] as Map<String, Any>)
            )
        }
    }
}