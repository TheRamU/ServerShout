package io.github.theramu.servershout.common

import io.github.theramu.servershout.common.model.ShoutChannel
import io.github.theramu.servershout.common.service.ShoutChannelService
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 17:26
 */
abstract class ServerShoutProxyApi protected constructor() : ServerShoutApi() {
    val shoutChannelService = ShoutChannelService()

    override fun load() {
        super.load()
        loadShoutChannels()
    }

    override fun removeCache(uuid: UUID) {
        super.removeCache(uuid)
        shoutChannelService.removePlayer(uuid)
    }

    private fun loadShoutChannels() {
        logger.info("Loading shout channels...")
        shoutChannelService.reset()
        val shoutConfig = configLoader.shoutConfig
        shoutConfig.getKeys("channels").forEach { key ->
            val shoutChannel = shoutConfig.get("channels.$key", ShoutChannel::class.java)
            shoutChannel.name = key
            shoutChannelService.addChannel(shoutChannel)
        }
    }
}