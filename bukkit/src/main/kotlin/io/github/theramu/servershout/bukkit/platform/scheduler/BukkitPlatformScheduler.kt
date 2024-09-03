package io.github.theramu.servershout.bukkit.platform.scheduler

import io.github.theramu.servershout.bukkit.ServerShoutBukkitPlugin
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduler
import org.bukkit.Bukkit

/**
 * @author TheRamU
 * @since 2024/8/28 19:28
 */
class BukkitPlatformScheduler : PlatformScheduler {

    private val scheduler get() = Bukkit.getScheduler()
    private val plugin = ServerShoutBukkitPlugin.getInstance()

    override fun run(task: Runnable, delay: Long): PlatformScheduledTask {
        val bukkitTask = scheduler.runTaskLater(plugin, task, delay / 50)
        return BukkitPlatformScheduledTask(bukkitTask)
    }

    override fun run(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val bukkitTask = scheduler.runTaskTimer(plugin, task, delay / 50, period / 50)
        return BukkitPlatformScheduledTask(bukkitTask)
    }

    override fun runAsync(task: Runnable, delay: Long): PlatformScheduledTask {
        val bukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, delay / 50)
        return BukkitPlatformScheduledTask(bukkitTask)
    }

    override fun runAsync(task: Runnable, delay: Long, period: Long): PlatformScheduledTask {
        val bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, task, delay / 50, period / 50)
        return BukkitPlatformScheduledTask(bukkitTask)
    }
}