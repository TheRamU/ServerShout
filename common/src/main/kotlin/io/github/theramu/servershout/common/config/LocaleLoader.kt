package io.github.theramu.servershout.common.config

import io.github.theramu.servershout.common.ServerShoutApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * @author TheRamU
 * @since 2024/08/20 04:17
 */
class LocaleLoader {

    private val api get() = ServerShoutApi.api
    private val dataFolder get() = api.dataFolder

    fun loadLocale() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        saveLocale()
    }

    fun getSystemLocale(): EnumLocale {
        return EnumLocale.getByName(getSystemLocaleName()) ?: EnumLocale.EN_US
    }

    fun getSystemLocaleName(): String {
        val locale = Locale.getDefault()
        return locale.language + "_" + locale.country
    }

    fun saveLocaleResource(resourcePath: String, overwrite: Boolean = false) {
        val file = File(dataFolder, resourcePath)
        if (file.exists() && !overwrite) return
        val inputStream = getLocaleResource(resourcePath) ?: throw IOException("Locale resource not found: $resourcePath")
        writeToLocal(inputStream, file)
    }

    private fun getJarEntries(folderPath: String): List<JarEntry> {
        val jarPath = Paths.get(this::class.java.getProtectionDomain().codeSource.location.toURI())
        JarFile(jarPath.toFile()).use { jarFile ->
            return jarFile.entries().asSequence().filter { !it.isDirectory && it.name.startsWith(folderPath) }.toList()
        }
    }

    private fun writeToLocal(input: InputStream, file: File) {
        val parent = File(file.parent)
        parent.mkdirs()
        FileOutputStream(file).use { output ->
            input.use { inputStream ->
                inputStream.copyTo(output)
            }
        }
    }

    private fun getResource(resourcePath: String): InputStream? {
        return this::class.java.getResourceAsStream("/$resourcePath")
    }

    fun getLocaleResource(resourcePath: String): InputStream? {
        return getResource("locale/${getSystemLocale().localeName}/${resourcePath}")
    }

    private fun saveLocale() {
        val folder = File(dataFolder, "locale")
        if (folder.exists()) return
        folder.mkdirs()
        for (entity in getJarEntries("locale/")) {
            getResource(entity.name)?.let { inputStream ->
                writeToLocal(inputStream, File(dataFolder, entity.name))
            }
        }
    }

    enum class EnumLocale(val localeName: String) {

        EN_US("en_US"),
        ZH_CN("zh_CN"),
        ZH_TW("zh_TW");

        companion object {
            private val localeMap = entries.associateBy(EnumLocale::localeName)

            @JvmStatic
            fun getByName(n: String): EnumLocale? = localeMap[n]
        }
    }
}