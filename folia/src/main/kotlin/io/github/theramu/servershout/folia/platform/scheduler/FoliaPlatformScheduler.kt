package io.github.theramu.servershout.folia.platform.scheduler

import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduler
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

/**
 * @author TheRamU
 * @since 2024/10/11 18:09
 */
class FoliaPlatformScheduler(
    private val plugin: Plugin
) : PlatformScheduler {

    private val scheduler get() = Bukkit.getGlobalRegionScheduler()
    private val asyncScheduler get() = Bukkit.getAsyncScheduler()

    override fun run(task: Runnable, delay: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.runDelayed(plugin, { task.run() }, delay / 50)
        return FoliaPlatformScheduledTask(scheduledTask)
    }

    override fun run(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.runAtFixedRate(plugin, { task.run() }, delay / 50, period / 50)
        return FoliaPlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long): PlatformScheduledTask {
        val scheduledTask = asyncScheduler.runDelayed(plugin, { task.run() }, delay, TimeUnit.MILLISECONDS)
        return FoliaPlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val scheduledTask = asyncScheduler.runAtFixedRate(plugin, { task.run() }, delay, period, TimeUnit.MILLISECONDS)
        return FoliaPlatformScheduledTask(scheduledTask)
    }
}