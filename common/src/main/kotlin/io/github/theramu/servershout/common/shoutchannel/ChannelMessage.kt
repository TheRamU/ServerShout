package io.github.theramu.servershout.common.shoutchannel

import io.github.theramu.servershout.common.platform.player.PlatformProxyPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer

/**
 * @author TheRamU
 * @since 2024/8/28 17:41
 */
class ChannelMessage(
    val id: String,
    val channel: ShoutChannel,
    val sender: PlatformProxyPlayer,
    val server: PlatformServer,
    val content: String,
    val timestamp: Long
)