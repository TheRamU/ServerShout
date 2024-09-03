package io.github.theramu.servershout.bungee.platform.scheduler

import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import net.md_5.bungee.api.scheduler.ScheduledTask

/**
 * @author TheRamU
 * @since 2024/8/28 19:30
 */
class BungeePlatformScheduledTask(
    override val handle: ScheduledTask
) : PlatformScheduledTask {
    override fun cancel() {
        handle.cancel()
    }
}