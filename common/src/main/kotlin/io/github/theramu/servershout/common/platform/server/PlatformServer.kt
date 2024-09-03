package io.github.theramu.servershout.common.platform.server

import io.github.theramu.servershout.common.platform.PlatformAudience
import io.github.theramu.servershout.common.platform.player.PlatformPlayer

/**
 * @author TheRamU
 * @since 2024/8/27 1:27
 */
interface PlatformServer : PlatformAudience {
    val name: String
    val players: List<PlatformPlayer>
}