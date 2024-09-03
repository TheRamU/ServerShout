package io.github.theramu.servershout.common.config.settings

import java.io.Serializable

/**
 * @author TheRamU
 * @since 2024/08/19 02:56
 */
data class DatabaseSettings(
    val enabled: Boolean,
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val options: String,
    val tablePrefix: String,
    val poolSettings: PoolSettings
) : Serializable {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): DatabaseSettings {
            @Suppress("UNCHECKED_CAST")
            return DatabaseSettings(
                enabled = map["enabled"] as Boolean,
                host = map["host"] as String,
                port = map["port"] as Int,
                database = map["database"] as String,
                username = map["username"] as String,
                password = map["password"] as String,
                options = map["options"] as String,
                tablePrefix = map["table-prefix"] as String,
                poolSettings = PoolSettings.deserialize(map["pool"] as Map<String, Any>)
            )
        }
    }

    class PoolSettings(
        val maxSize: Int,
        val minIdle: Int,
        val maxLifetime: Long,
        val connectionTimeout: Long,
        val idleTimeout: Long
    ) : Serializable {
        companion object {
            @JvmStatic
            fun deserialize(map: Map<String, Any>): PoolSettings {
                return PoolSettings(
                    maxSize = map["max-size"] as Int,
                    minIdle = map["min-idle"] as Int,
                    maxLifetime = (map["max-lifetime"] as Number).toLong(),
                    connectionTimeout = (map["connection-timeout"] as Number).toLong(),
                    idleTimeout = (map["idle-timeout"] as Number).toLong(),
                )
            }
        }
    }
}
