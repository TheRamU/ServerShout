package io.github.theramu.servershout.common

import io.github.theramu.servershout.common.shoutchannel.ShoutChannel
import io.github.theramu.servershout.common.shoutchannel.ShoutChannelService

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