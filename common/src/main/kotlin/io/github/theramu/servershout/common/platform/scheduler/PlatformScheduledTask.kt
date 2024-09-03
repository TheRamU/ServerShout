package io.github.theramu.servershout.common.platform.scheduler

/**
 * @author TheRamU
 * @since 2024/8/28 19:13
 */
interface PlatformScheduledTask {
    val handle: Any
    fun cancel()
}