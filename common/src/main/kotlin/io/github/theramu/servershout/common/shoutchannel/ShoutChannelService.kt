package io.github.theramu.servershout.common.shoutchannel

import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.ServerShoutProxyApi
import io.github.theramu.servershout.common.exception.ServiceException
import io.github.theramu.servershout.common.platform.ProxyPlatform
import io.github.theramu.servershout.common.platform.player.PlatformProxyPlayer
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import io.github.theramu.servershout.common.util.ColorUtil
import io.github.theramu.servershout.common.util.UuidUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.luckperms.api.LuckPermsProvider
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern
import kotlin.concurrent.withLock

/**
 * @author TheRamU
 * @since 2024/8/22 2:21
 */
class ShoutChannelService {

    private val commandPattern = Pattern.compile("""/(servershoutjoin|servershoutmute|servershoutmuteplayer|servershoutmutechannel|servershoutmuteglobal|servershoutunmute) ([a-f0-9]{32})""")
    private val buttonPattern = Pattern.compile("""BUTTON\((JOIN|MUTE|MUTE_PLAYER|MUTE_CHANNEL|MUTE_GLOBAL|UNMUTE),"([^"]*)"(?:,"([^"]*)")?\)""")

    private val api get() = ServerShoutApi.api as ServerShoutProxyApi
    private val platform get() = api.platform as ProxyPlatform
    private val pluginManager get() = platform.pluginManager
    private val proxyServer get() = platform.proxyServer
    private val tokenService get() = api.tokenService
    private val balanceService get() = api.balanceService
    private val configLoader get() = api.configLoader
    private val databaseSettings get() = configLoader.databaseSettings
    private val shoutGlobalSettings get() = configLoader.shoutGlobalSettings
    private val channels = mutableListOf<ShoutChannel>()
    private val messageQueue = Collections.synchronizedList(mutableListOf<ChannelMessage>())
    private val historyMessages = mutableMapOf<String, ChannelMessage>()
    private val mutePlayerMap = mutableMapOf<UUID, MutableList<UUID>>()
    private val muteChannelMap = mutableMapOf<UUID, MutableList<String>>()
    private var timerTask: PlatformScheduledTask? = null
    private val lock = ReentrantLock()

    fun addChannel(channel: ShoutChannel) {
        if (channels.contains(channel)) {
            throw IllegalArgumentException("Channel with name ${channel.name} already exists")
        }
        channels.add(channel)
    }

    fun getChannel(name: String): ShoutChannel? {
        return channels.find { it.name == name }
    }

    fun getChannels(): MutableList<ShoutChannel> {
        return channels.toMutableList()
    }

    fun removeChannel(name: String) {
        val channel = getChannel(name)
        if (channel != null) {
            channels.remove(channel)
        }
    }

    fun reset() {
        channels.clear()
        messageQueue.clear()
        historyMessages.clear()
        mutePlayerMap.clear()
        muteChannelMap.clear()
        startTimer()
    }

    fun removePlayer(uuid: UUID) {
        mutePlayerMap.remove(uuid)
        muteChannelMap.remove(uuid)
    }

    fun shout(player: PlatformProxyPlayer, message: String): Boolean {
        if (handelInternalCommand(player, message)) {
            return true
        }

        val currentServer = player.currentServer
        val serverName = currentServer.name

        if (!shoutGlobalSettings.serverList.isAllowed(serverName)) {
            return false
        }

        val channel = channels.find {
            it.enabled &&
                    it.senderServerList.isAllowed(serverName) &&
                    (it.permission.isEmpty() || player.hasPermission(it.permission)) &&
                    it.chatPrefix.isNotEmpty() &&
                    message.startsWith(it.chatPrefix.trimEnd())
        } ?: return false

        if (isSending(player)) {
            player.sendLanguageMessage("message.shout.sending")
            return true
        }
        pruneHistoryMessages()
        val coolingMessage = getCoolingMessage(player, channel)

        if (coolingMessage != null) {
            val remaining = (channel.cooldown - (System.currentTimeMillis() - coolingMessage.timestamp) / 1000)
            player.sendLanguageMessage("message.shout.cooldown", remaining)
            return true
        }

        val content = message.removePrefix(channel.chatPrefix.trimEnd()).trimStart()

        if (content.isEmpty() && !channel.allowEmptyMessage) {
            player.sendLanguageMessage("message.shout.empty")
            return true
        }

        val messageId = UuidUtil.simpleUuid()
        lock.withLock {
            messageQueue.add(ChannelMessage(messageId, channel, player, currentServer, content, System.currentTimeMillis()))
        }
        return true
    }

    private fun isSending(player: PlatformProxyPlayer) = lock.withLock {
        messageQueue.any { it.sender.uuid == player.uuid }
    }

    private fun getCoolingMessage(player: PlatformProxyPlayer, channel: ShoutChannel): ChannelMessage? {
        return historyMessages.values.find {
            it.sender.uuid == player.uuid && it.channel == channel &&
                    System.currentTimeMillis() - it.timestamp < channel.cooldown * 1000
        }
    }

    private fun pruneHistoryMessages() {
        val currentTime = System.currentTimeMillis()
        val iter = historyMessages.iterator()
        while (iter.hasNext()) {
            val message = iter.next().value
            if (currentTime - message.timestamp > message.channel.expiration * 1000) {
                iter.remove()
            }
        }
    }

    private fun parseComponent(str: String, messageId: String, replaceFun: (String) -> String = { it }): Component {
        val component = Component.text()

        val matcher = buttonPattern.matcher(str)
        var lastEnd = 0

        val legacySection = LegacyComponentSerializer.legacySection()

        while (matcher.find()) {
            // 按钮前的文本
            if (matcher.start() > lastEnd) {
                component.append(legacySection.deserialize(replaceFun(str.substring(lastEnd, matcher.start()))))
            }

            val type = matcher.group(1)
            val text = matcher.group(2)
            val hover = matcher.group(3)
            var buttonComponent = legacySection.deserialize(replaceFun(text)).clickEvent(
                when (type) {
                    "JOIN" -> ClickEvent.runCommand("/servershoutjoin $messageId")
                    "MUTE" -> ClickEvent.runCommand("/servershoutmute $messageId")
                    "MUTE_PLAYER" -> ClickEvent.runCommand("/servershoutmuteplayer $messageId")
                    "MUTE_CHANNEL" -> ClickEvent.runCommand("/servershoutmutechannel $messageId")
                    "MUTE_GLOBAL" -> ClickEvent.runCommand("/servershoutmuteglobal $messageId")
                    "UNMUTE" -> ClickEvent.runCommand("/servershoutunmute $messageId")
                    else -> throw IllegalArgumentException("Unknown button type $type")
                }
            )
            hover?.let {
                buttonComponent = buttonComponent.hoverEvent(Component.text(it))
            }
            component.append(buttonComponent)

            lastEnd = matcher.end()
        }

        // 最后一个按钮后的文本
        if (lastEnd < str.length) {
            component.append(legacySection.deserialize(replaceFun(str.substring(lastEnd))))
        }

        return component.build()
    }

    private fun handelInternalCommand(player: PlatformProxyPlayer, message: String): Boolean {
        val joinMatcher = commandPattern.matcher(message)
        if (!joinMatcher.matches()) {
            return false
        }

        if (!shoutGlobalSettings.serverList.isAllowed(player.currentServer.name)) {
            player.sendLanguageMessage("message.join.illegal-server")
            return true
        }

        pruneHistoryMessages()
        val joinType = joinMatcher.group(1)
        val messageId = joinMatcher.group(2)
        val channelMessage = historyMessages[messageId]
        if (channelMessage == null) {
            player.sendLanguageMessage("message.join.expired")
            return true
        }
        if (joinType == "servershoutjoin") {
            // 相同服务器，不处理
            if (channelMessage.server.name == player.currentServer.name) {
                player.sendLanguageMessage("message.join.same-server")
                return true
            }
            val channel = channelMessage.channel
            if (!channel.receiverServerList.isAllowed(player.currentServer.name)) {
                player.sendLanguageMessage("message.join.illegal-server")
                return true
            }
            // 传送玩家
            player.connect(channelMessage.server)
            return true
        }

        val channelName = channelMessage.channel.name!!
        val senderUuid = channelMessage.sender.uuid
        val playerUuid = player.uuid
        if (senderUuid == playerUuid) {
            player.sendLanguageMessage("message.mute.self")
            return true
        }

        // 处理屏蔽
        when (joinType) {
            "servershoutmute" -> {
                val selector = configLoader.language("message.mute.selector")
                player.sendMessage(parseComponent(ColorUtil.translateColor(configLoader.messagePrefix + selector), messageId))
            }

            "servershoutmuteplayer" -> {
                val mutePlayerList = mutePlayerMap.getOrPut(playerUuid) { mutableListOf() }
                if (!mutePlayerList.contains(senderUuid)) {
                    mutePlayerList.add(senderUuid)
                    player.sendLanguageMessage("message.mute.player", channelMessage.sender.name)
                }
            }

            "servershoutmutechannel" -> {
                val muteChannelList = muteChannelMap.getOrPut(playerUuid) { mutableListOf() }
                if (!muteChannelList.contains(channelName)) {
                    muteChannelList.add(channelName)
                    player.sendLanguageMessage("message.mute.channel", channelName)
                }
            }

            "servershoutmuteglobal" -> {
                val muteChannelList = muteChannelMap.getOrPut(playerUuid) { mutableListOf() }
                muteChannelList.clear()
                muteChannelList.addAll(channels.mapNotNull { it.name })
                player.sendLanguageMessage("message.mute.global", channelMessage.sender.name, channelName)
            }

            "servershoutunmute" -> {
                mutePlayerMap.remove(playerUuid)
                muteChannelMap.remove(playerUuid)
                player.sendLanguageMessage("message.mute.unmute")
            }
        }
        return true
    }

    private fun startTimer() {
        timerTask?.cancel()
        timerTask = platform.scheduler.runAsync({
            val messagesToProcess = mutableListOf<ChannelMessage>()
            lock.withLock {
                messagesToProcess.addAll(messageQueue)
                messageQueue.clear()
            }

            // 在锁外处理消息
            val currentTime = System.currentTimeMillis()
            for (message in messagesToProcess) {
                if (currentTime - message.timestamp > 10000) {
                    if (message.sender.isOnline()) message.sender.sendLanguageMessage("message.shout.failed")
                    continue
                }
                try {
                    shoutMessage(message)
                } catch (e: Exception) {
                    if (message.sender.isOnline()) message.sender.sendLanguageMessage("message.shout.failed")
                    e.printStackTrace()
                }
            }
        }, 0, 50)
    }

    private fun shoutMessage(channelMessage: ChannelMessage) {
        val sender = channelMessage.sender
        val server = channelMessage.server
        val channel = channelMessage.channel
        val content = channelMessage.content

        var tokenCostName: String? = null
        var balanceAmount = 1L

        val isEmpty = content.isEmpty()
        val channelName = channel.name ?: ""

        // 只有数据库启用时才检查余额
        if (databaseSettings.enabled) {
            val cost = if (isEmpty) {
                channel.tokenCostEmpty
            } else {
                channel.tokenCostFull
            }
            val token = tokenService.getToken(cost.name)
            // 若 token 不存在则不检查余额
            if (token != null) {
                val replacedTokenName = shoutGlobalSettings.tokenMap[token.name] ?: token.name
                if (!balanceService.hasBalance(sender.name, token, cost.amount)) {
                    if (sender.isOnline()) sender.sendLanguageMessage("message.shout.token-not-enough", replacedTokenName, cost.amount)
                    return
                }
                try {
                    balanceService.takeBalance(sender.name, token, cost.amount)
                } catch (_: ServiceException) {
                    if (sender.isOnline()) sender.sendLanguageMessage("message.shout.token-not-enough", replacedTokenName, cost.amount)
                    return
                }
                // 扣费成功
                tokenCostName = replacedTokenName
                balanceAmount = balanceService.getBalanceAmount(sender.name, token)
            }
        }

        val serverName = server.name
        val receiverServers = proxyServer.servers.filter {
            channel.receiverServerList.isAllowed(it.name) && shoutGlobalSettings.serverList.isAllowed(it.name)
        }
        val format = if (isEmpty) channel.formatEmpty else channel.formatFull
        val formattedMessage = format.joinToString("\n&r")

        val replacedServerName = shoutGlobalSettings.serverMap[serverName] ?: serverName
        val replaceFun = fun(str: String) = str.replace("{server}", replacedServerName)
            .replace("{server_players}", server.players.size.toString())
            .replace("{channel}", channelName)
            .replaceLuckPermsPlaceholders(sender)
            .replace("{player}", sender.name)
            .replace("{message}", content)

        val component = parseComponent(ColorUtil.translateColor(formattedMessage), channelMessage.id, replaceFun)

        receiverServers.filter { channel.localBroadcast || it.name != serverName }.forEach {
            for (player in it.players) {
                if (mutePlayerMap[player.uuid]?.contains(sender.uuid) == true || muteChannelMap[player.uuid]?.contains(channelName) == true) {
                    // 屏蔽消息
                    continue
                }
                player.sendMessage(component)
            }
        }

        if (sender.isOnline()) {
            if (!channel.localBroadcast) sender.sendMessage(component)
            if (tokenCostName != null) {
                sender.sendLanguageMessage("message.shout.success", tokenCostName, balanceAmount)
            } else {
                sender.sendLanguageMessage("message.shout.success-free")
            }
        }

        historyMessages[channelMessage.id] = channelMessage

        api.sendUpdate(sender.name)

        if (shoutGlobalSettings.logging) {
            if (isEmpty) {
                platform.consoleCommandSender.sendLanguageMessage("message.logging.format-empty", channelName, replacedServerName, sender.name)
            } else {
                platform.consoleCommandSender.sendLanguageMessage("message.logging.format-full", channelName, replacedServerName, sender.name, content)
            }
        }
    }

    private fun String.replaceLuckPermsPlaceholders(player: PlatformProxyPlayer): String {
        if (!pluginManager.isPluginEnabled("LuckPerms") && !pluginManager.isPluginEnabled("luckperms")) {
            return this
        }
        val luckPermsApi = LuckPermsProvider.get()
        val metaData = luckPermsApi.userManager.getUser(player.uuid)?.cachedData?.metaData
        val matcher = Pattern.compile("\\{luckperms_meta_([^}]+)}").matcher(this)
        val replaced = StringBuffer()
        while (matcher.find()) {
            val value = when (val key = matcher.group(1)) {
                "prefix" -> metaData?.prefix
                "suffix" -> metaData?.suffix
                else -> metaData?.getMetaValue(key)
            }
            matcher.appendReplacement(replaced, ColorUtil.translateColor(value ?: ""))
        }
        matcher.appendTail(replaced)
        return replaced.toString()
    }
}