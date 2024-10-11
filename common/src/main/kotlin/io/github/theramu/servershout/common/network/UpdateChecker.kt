package io.github.theramu.servershout.common.network

import com.google.gson.JsonParser
import io.github.theramu.servershout.common.BuildConstants
import io.github.theramu.servershout.common.ServerShoutApi
import io.github.theramu.servershout.common.platform.PlatformAudience
import io.github.theramu.servershout.common.platform.player.PlatformPlayer
import io.github.theramu.servershout.common.platform.scheduler.PlatformScheduledTask
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @author TheRamU
 * @since 2024/8/30 4:13
 */
class UpdateChecker {

    private val releaseUrl = "https://api.github.com/repos/TheRamU/ServerShout/releases"
    private val api get() = ServerShoutApi.api
    private val platform get() = api.platform
    private val scheduler get() = platform.scheduler
    private val logger get() = api.logger
    private val configLoader get() = api.configLoader
    private val settings get() = configLoader.updateCheckSettings
    val currentVersion = BuildConstants.VERSION
    var latestVersion: String private set
    var versionBehind: Int private set
    var downloadUrl: String private set
    private var timerTask: PlatformScheduledTask? = null
    private val notifiedPlayers = mutableSetOf<UUID>()

    init {
        latestVersion = currentVersion
        versionBehind = 0
        downloadUrl = ""
    }

    fun check() {
        try {
            request()
            if (hasUpdate()) sendUpdateMessage(platform.consoleCommandSender)
        } catch (e: Exception) {
            logger.warn("Failed to check for updates", e)
        }
    }

    fun hasUpdate(): Boolean {
        return latestVersion != currentVersion
    }

    fun notifyUpdate(receiver: PlatformPlayer) {
        if (settings.enabled && hasUpdate() && settings.notify
            && receiver.hasPermission("servershout.update.notify")
            && receiver.uuid !in notifiedPlayers
        ) {
            scheduler.run({
                if (!receiver.isOnline()) return@run
                sendUpdateMessage(receiver)
                notifiedPlayers.add(receiver.uuid)
            }, 500)
        }
    }

    fun sendUpdateMessage(receiver: PlatformAudience) {
        logger.sendLanguageMessage(receiver.handle, "message.update-available", currentVersion, latestVersion, versionBehind, downloadUrl)
    }

    fun startTimer() {
        timerTask?.cancel()
        timerTask = scheduler.runAsync({
            if (settings.enabled) {
                check()
            }
        }, 1000, 1000 * 60 * 60 * 8)
    }

    fun stopTimer() {
        timerTask?.cancel()
    }

    fun removeNotified(uuid: UUID) {
        notifiedPlayers.remove(uuid)
    }

    private fun request() {
        URL(releaseUrl).openStream().use { stream ->
            val releaseArray = JsonParser().parse(InputStreamReader(stream)).asJsonArray
            val latestRelease = releaseArray.first().asJsonObject
            var tagName = latestRelease["tag_name"].asString
            if (tagName.startsWith("v")) {
                tagName = tagName.substring(1)
            }
            if (tagName <= currentVersion) {
                return
            }
            var behind = releaseArray.indexOfFirst {
                var releaseTag = it.asJsonObject["tag_name"].asString
                if (releaseTag.startsWith("v")) {
                    releaseTag = releaseTag.substring(1)
                }
                releaseTag == currentVersion
            }
            if (behind == -1) {
                behind = releaseArray.size()
            }

            val body = latestRelease["body"].asString
            downloadUrl = parseDownloadUrl(body)
            latestVersion = tagName
            versionBehind = behind
        }
    }

    private fun parseDownloadUrl(body: String): String {
        val index = body.indexOf("\r\n\r\n")
        if (index == -1) {
            throw RuntimeException("Invalid release body")
        }

        val bodyHeader = body.substring(0, index)
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bodyHeader.byteInputStream())
        val downloadUrls = document.getElementsByTagName("download-url")
        val urls = mutableMapOf<String, String>()
        for (i in 0 until downloadUrls.length) {
            val downloadUrl = downloadUrls.item(i)
            val locales = downloadUrl.attributes.getNamedItem("locales").textContent.split(",")
            val url = downloadUrl.attributes.getNamedItem("url").textContent
            for (locale in locales) {
                urls[locale.trim()] = url
            }
        }

        return urls[getSystemLocaleName()] ?: urls["en_US"] ?: urls.values.first()
    }

    private fun getSystemLocaleName(): String {
        val locale = Locale.getDefault()
        return locale.language + "_" + locale.country
    }
}