package io.github.theramu.servershout.velocity.platform.scheduler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduler
import io.github.theramu.servershout.velocity.ServerShoutVelocityApi
import io.github.theramu.servershout.velocity.ServerShoutVelocityPlugin
import java.time.Duration

/**
 * @author TheRamU
 * @since 2024/8/28 19:09
 */
class VelocityPlatformScheduler : PlatformScheduler {

    private val api get() = ServerShoutApi.api as ServerShoutVelocityApi
    private val plugin get() = ServerShoutVelocityPlugin.getInstance()
    private val scheduler get() = api.proxy.scheduler

    override fun run(task: Runnable, delay: Long): VelocityPlatformScheduledTask {
        val scheduledTask = scheduler.buildTask(plugin, task).delay(Duration.ofMillis(delay)).schedule()
        return VelocityPlatformScheduledTask(scheduledTask)
    }

    override fun run(task: Runnable, delay: Long, period: Long): VelocityPlatformScheduledTask {
        val scheduledTask = scheduler.buildTask(plugin, task).delay(Duration.ofMillis(delay)).repeat(Duration.ofMillis(period)).schedule()
        return VelocityPlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.buildTask(plugin, task).delay(Duration.ofMillis(delay)).schedule()
        return VelocityPlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.buildTask(plugin, task).delay(Duration.ofMillis(delay)).repeat(Duration.ofMillis(period)).schedule()
        return VelocityPlatformScheduledTask(scheduledTask)
    }
}