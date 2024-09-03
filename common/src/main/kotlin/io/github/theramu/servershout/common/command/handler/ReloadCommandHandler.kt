package io.github.theramu.servershout.common.command.handler

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.CommandHandler
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/25 4:42
 */
class ReloadCommandHandler : CommandHandler {

    private val api get() = ServerShoutApi.api
    override val arguments = arrayOf("reload")
    override val permission = "servershout.reload"
    override val playerOnly = false
    override val usage get() = "message.commands.reload.usage"

    override fun handle(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        api.load()
        sender.sendLanguageMessage("message.commands.reload.success")
    }
}