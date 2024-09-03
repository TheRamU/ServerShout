package io.github.theramu.servershout.common.platform.scheduler

/**
 * @author TheRamU
 * @since 2024/8/28 18:12
 */
interface PlatformScheduler {

    fun run(task: Runnable, delay: Long): PlatformScheduledTask

    fun run(task: Runnable, delay: Long, period: Long): PlatformScheduledTask

    fun runAsync(task: Runnable, delay: Long): PlatformScheduledTask

    fun runAsync(task: Runnable, delay: Long, period: Long): PlatformScheduledTask
}