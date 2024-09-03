package io.github.theramu.servershout.common.platform.player

import io.github.theramu.servershout.common.platform.command.PlatformCommandSender
import java.util.UUID

/**
 * @author TheRamU
 * @since 2024/8/25 2:22
 */
interface PlatformPlayer : PlatformCommandSender {
    val name: String
    val uuid: UUID
    fun isOnline(): Boolean
}