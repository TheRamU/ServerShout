package io.github.theramu.servershout.common.config.settings

import java.io.Serializable

/**
 * @author TheRamU
 * @since 2024/9/1 15:56
 */
class UpdateCheckSettings(
    val enabled: Boolean,
    val notify: Boolean
) : Serializable {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): UpdateCheckSettings {
            return UpdateCheckSettings(
                enabled = map["enabled"] as Boolean,
                notify = map["notify"] as Boolean
            )
        }
    }
}