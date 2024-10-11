package io.github.theramu.servershout.folia.platform.scheduler

import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

/**
 * @author TheRamU
 * @since 2024/10/11 18:23
 */
class FoliaPlatformScheduledTask(
    override val handle: ScheduledTask
) : PlatformScheduledTask {
    override fun cancel() {
        handle.cancel()
    }
}