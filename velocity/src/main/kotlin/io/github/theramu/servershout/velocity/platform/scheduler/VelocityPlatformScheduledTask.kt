package io.github.theramu.servershout.velocity.platform.scheduler

import com.velocitypowered.api.scheduler.ScheduledTask
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask

/**
 * @author TheRamU
 * @since 2024/8/28 19:13
 */
class VelocityPlatformScheduledTask(
    override val handle: ScheduledTask
) : PlatformScheduledTask {
    override fun cancel() {
        handle.cancel()
    }
}