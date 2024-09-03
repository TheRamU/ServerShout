package io.github.theramu.servershout.common.config.settings

import java.io.Serializable

/**
 * @author TheRamU
 * @since 2024/8/20 22:18
 */
data class ServerListSettings(
    val mode: Mode,
    val servers: List<String>
) : Serializable {

    companion object {
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun deserialize(map: Map<String, Any>): ServerListSettings {
            return ServerListSettings(
                mode = Mode.getByName(map["mode"] as String) ?: Mode.WHITELIST,
                servers = map["servers"] as List<String>
            )
        }
    }

    fun isAllowed(server: String): Boolean {
        return when {
            servers.isEmpty() -> true
            mode == Mode.WHITELIST -> servers.contains(server)
            mode == Mode.BLACKLIST -> !servers.contains(server)
            else -> false
        }
    }

    enum class Mode(val modeName: String) {
        WHITELIST("whitelist"),
        BLACKLIST("blacklist");

        companion object {
            private val map = Mode.entries.associateBy(Mode::modeName)

            @JvmStatic
            fun getByName(name: String): Mode? = Mode.map[name]
        }
    }
}