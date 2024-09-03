package io.github.theramu.servershout.common.platform.command

import io.github.theramu.servershout.common.platform.PlatformAudience

/**
 * @author TheRamU
 * @since 2024/8/25 2:22
 */
interface PlatformCommandSender : PlatformAudience {
    fun hasPermission(permission: String): Boolean
}