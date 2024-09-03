package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/25 3:24
 */
class InfoCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    override val arguments = arrayOf<String>()
    override val permission = null
    override val playerOnly = false
    override val usage = null

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        sender.sendMessage("")
        sender.sendMessage(" §e§lServerShout §f§lv${api.version} §r§7by TheRamU")
        sender.sendMessage("")
    }
}