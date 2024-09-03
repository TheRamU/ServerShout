package io.github.theramu.servershout.common.command

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.command.handler.*
import io.github.theramu.servershout.common.exception.ServiceException
import io.github.theramu.servershout.common.platform.command.PlatformCommandSender

/**
 * @author TheRamU
 * @since 2024/8/25 2:18
 */
class CommandManager {

    private val api get() = ServerShoutApi.api
    private val handlerList: MutableList<CommandHandler> = mutableListOf()

    init {
        registerHandlers(
            InfoCommandHandler(),
            HelpCommandHandler(),
            ReloadCommandHandler(),
            TokenListCommandHandler(),
            TokenCreateCommandHandler(),
            TokenDeleteCommandHandler(),
            BalanceGetCommandHandler(),
            BalanceSetCommandHandler(),
            BalanceGiveCommandHandler(),
            BalanceTakeCommandHandler(),
            MigrateCommandHandler()
        )
    }

    fun execute(sender: PlatformCommandSender, alias: String, args: Array<String>) {
        // 查找命令处理器
        val handlerList = handlerList.filter {
            args.isEmpty() == it.arguments.isEmpty()
        }.toMutableList()
        if (args.isNotEmpty()) {
            for (i in args.indices) {
                handlerList.removeIf { handler ->
                    val arguments = handler.arguments
                    arguments.size > i && !isVariable(arguments[i]) && !arguments[i].equals(args[i], ignoreCase = true)
                }
            }
        }

        if (handlerList.isEmpty()) {
            sender.sendLanguageMessage("message.command.unknown", alias)
            return
        }
        val handler = handlerList[0]
        if (!handler.hasPermission(sender)) {
            sender.sendLanguageMessage("message.command.no-permission")
            return
        }

        // 判断发送者是否合法
        if (!handler.isAllowedSender(sender)) {
            sender.sendLanguageMessage("message.command.not-player")
            return
        }

        if (handlerList.size > 1 || (args.size < handler.arguments.size && args.size < handler.arguments.count { !isNullableVariable(it) })) {
            sender.sendLanguageMessage("message.command.parameter-error", alias)
            return
        }

        try {
            handler.handle(sender, alias, args)
        } catch (e: ServiceException) {
            val message = e.message
            val language = e.language
            if (message != null) {
                sender.sendMessage(api.configLoader.messagePrefix + message)
            } else if (language != null) {
                sender.sendLanguageMessage(language, *e.args)
            }
        }
    }

    fun suggest(sender: PlatformCommandSender, alias: String, args: Array<String>): List<String> {
        val suggestions = mutableSetOf<String>()
        loop@ for (handler in handlerList) {
            // 检查用户是否有权限执行该命令
            if (handler.permission != null && !sender.hasPermission(handler.permission!!)) {
                continue
            }
            val handlerArgs = handler.arguments

            // 检查命令前缀是否匹配
            if (args.size > handlerArgs.size) {
                continue
            }
            for (i in args.indices) {
                if (!handlerArgs[i].startsWith(args[i], ignoreCase = true) && !isVariable(handlerArgs[i])) {
                    continue@loop
                }
            }

            val suggestion = handlerArgs[args.size - 1]
            if (isVariable(suggestion)) {
                suggestions.addAll(handler.variableSuggest(sender, args, suggestion))
            } else if (!handler.hidden) {
                suggestions.add(suggestion)
            }
        }
        return suggestions.toList()
    }

    fun getHandlerList(): MutableList<CommandHandler> {
        return handlerList.toMutableList()
    }

    fun registerHandler(handler: CommandHandler) {
        if (!handlerList.contains(handler)) {
            handlerList.add(handler)
        }
    }

    fun registerHandlers(vararg handlers: CommandHandler) {
        for (handler in handlers) {
            this.registerHandler(handler)
        }
    }

    fun unregisterHandler(handler: CommandHandler) {
        handlerList.remove(handler)
    }

    private fun isVariable(argument: String): Boolean {
        return argument.startsWith("<") && argument.endsWith(">")
    }

    private fun isNullableVariable(text: String): Boolean {
        if (!isVariable(text)) {
            return false
        }
        return text.endsWith("_NULLABLE>")
    }
}