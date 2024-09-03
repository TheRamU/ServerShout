package io.github.theramu.servershout.bukkit.platform.scheduler

import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import org.bukkit.scheduler.BukkitTask

/**
 * @author TheRamU
 * @since 2024/8/28 19:26
 */
class BukkitPlatformScheduledTask(
    override val handle: BukkitTask
) : PlatformScheduledTask {
    override fun cancel() {
        handle.cancel()
    }
}