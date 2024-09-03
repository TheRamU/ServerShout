package io.github.theramu.servershout.bungee.platform.scheduler

import io.github.theramu.servershout.bungee.ServerShoutBungeePlugin
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduler
import net.md_5.bungee.api.ProxyServer
import java.util.concurrent.TimeUnit

/**
 * @author TheRamU
 * @since 2024/8/28 19:31
 */
class BungeePlatformScheduler : PlatformScheduler {

    private val scheduler get() = ProxyServer.getInstance().scheduler
    private val plugin get() = ServerShoutBungeePlugin.getInstance()

    override fun run(task: Runnable, delay: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.schedule(plugin, task, delay, TimeUnit.MILLISECONDS)
        return BungeePlatformScheduledTask(scheduledTask)
    }

    override fun run(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.schedule(plugin, task, delay, period, TimeUnit.MILLISECONDS)
        return BungeePlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.schedule(plugin, task, delay, TimeUnit.MILLISECONDS)
        return BungeePlatformScheduledTask(scheduledTask)
    }

    override fun runAsync(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val scheduledTask = scheduler.schedule(plugin, task, delay, period, TimeUnit.MILLISECONDS)
        return BungeePlatformScheduledTask(scheduledTask)
    }
}