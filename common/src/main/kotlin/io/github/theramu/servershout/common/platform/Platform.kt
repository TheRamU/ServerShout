package io.github.theramu.servershout.common.platform

import io.github.theramu.servershout.common.platform.command.PlatformConsoleCommandSender
import io.github.theramu.servershout.common.platform.player.PlatformPlayer
import io.github.theramu.servershout.common.platform.plugin.PlatformPluginManager
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduler

/**
 * @author TheRamU
 * @since 2024/9/1 0:47
 */
interface Platform {
    val onlinePlayers: List<PlatformPlayer>
    val scheduler: PlatformScheduler
    val consoleCommandSender: PlatformConsoleCommandSender
    val pluginManager: PlatformPluginManager
}