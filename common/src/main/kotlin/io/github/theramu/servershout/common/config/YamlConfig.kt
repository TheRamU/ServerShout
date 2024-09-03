package io.github.theramu.servershout.common.config

import org.yaml.snakeyaml.Yaml
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * @author TheRamU
 * @since 2024/08/20 03:24
 */
class YamlConfig {

    private var configMap = mutableMapOf<String, Any>()
    private val cache = mutableMapOf<String, Any?>()
    private val lines = mutableListOf<String>()

    constructor(file: File) : this(FileInputStream(file))

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream, StandardCharsets.UTF_8))

    constructor(reader: Reader) {
        reader.use {
            val content = reader.readText()
            lines.addAll(content.lines())
            this.configMap = Yaml().load(content) ?: mutableMapOf()
        }
    }

    constructor(map: Map<String, Any>) {
        this.configMap = map.toMutableMap()
    }

    fun getMap() = configMap.toMutableMap()

    fun getLines() = lines.toList()

    fun getChar(path: String, default: Char? = null): Char {
        return (get(path, default) as Char)
    }

    fun getString(path: String, default: String? = null): String {
        return get(path, default) as String
    }

    fun getShort(path: String, default: Short? = null): Short {
        return (get(path, default) as Number).toShort()
    }

    fun getByte(path: String, default: Byte? = null): Byte {
        return (get(path, default) as Number).toByte()
    }

    fun getInt(path: String, default: Int? = null): Int {
        return (get(path, default) as Number).toInt()
    }

    fun getLong(path: String, default: Long? = null): Long {
        return (get(path, default) as Number).toLong()
    }

    fun getFloat(path: String, default: Float? = null): Float {
        return (get(path, default) as Number).toFloat()
    }

    fun getDouble(path: String, default: Double? = null): Double {
        return (get(path, default) as Number).toDouble()
    }

    fun getBoolean(path: String, default: Boolean? = null): Boolean {
        return get(path, default) as Boolean
    }

    fun getList(path: String, default: List<*>? = null): List<*> {
        return get(path, default) as List<*>
    }

    fun getStringList(path: String, default: List<String>? = null): List<String> {
        return getList(path, default).map { it.toString() }
    }

    fun getShortList(path: String, default: List<Short>? = null): List<Short> {
        return getList(path, default).map { (it as Number).toShort() }
    }

    fun getByteList(path: String, default: List<Byte>? = null): List<Byte> {
        return getList(path, default).map { (it as Number).toByte() }
    }

    fun getIntList(path: String, default: List<Int>? = null): List<Int> {
        return getList(path, default).map { (it as Number).toInt() }
    }

    fun getLongList(path: String, default: List<Long>? = null): List<Long> {
        return getList(path, default).map { (it as Number).toLong() }
    }

    fun getFloatList(path: String, default: List<Float>? = null): List<Float> {
        return getList(path, default).map { (it as Number).toFloat() }
    }

    fun getDoubleList(path: String, default: List<Double>? = null): List<Double> {
        return getList(path, default).map { (it as Number).toDouble() }
    }

    fun getBooleanList(path: String, default: List<Boolean>? = null): List<Boolean> {
        return getList(path, default).map { it as Boolean }
    }

    fun getMapList(path: String, default: List<Map<*, *>>? = null): List<Map<*, *>> {
        return getList(path, default).map { it as Map<*, *> }
    }

    fun getMap(path: String, default: Map<*, *>? = null): Map<*, *> {
        return get(path, default) as Map<*, *>
    }

    fun get(path: String, default: Any? = null): Any {
        if (path.isEmpty()) throw IllegalArgumentException("Path cannot be empty")

        if (!cache.containsKey(path)) {
            val keys = path.split(".")
            var current: Any? = configMap
            for (key in keys) {
                if (current is Map<*, *>) {
                    current = current[key]
                } else {
                    current = null
                    break
                }
            }
            cache[path] = current
        }
        return cache[path] ?: default ?: throw IllegalArgumentException("Invalid path: $path")
    }

    fun <T> get(path: String, clazz: Class<T>): T {
        if (path.isEmpty()) throw IllegalArgumentException("Path cannot be empty")

        val value = get(path)

        if (Serializable::class.java.isAssignableFrom(clazz)) {
            if (value !is Map<*, *>) {
                throw IllegalArgumentException("Invalid type for path: $path")
            }
            val method = clazz.getDeclaredMethod("deserialize", Map::class.java)
            method.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return method.invoke(null, value) as T
        }

        return clazz.cast(value)
    }

    fun getKeys(path: String, deep: Boolean = false): Set<String> {
        val keys = mutableSetOf<String>()
        val current = get(path) as? Map<*, *> ?: return keys
        for (key in current.keys) {
            keys.add(key.toString())
            if (deep) {
                val subPath = if (path.isEmpty()) key.toString() else "$path.$key"
                keys.addAll(getKeys(subPath, true))
            }
        }
        return keys
    }

    fun set(path: String, value: Any) {
        val keys = path.split(".")
        var current = configMap
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            if (!current.containsKey(key)) {
                current[key] = mutableMapOf<String, Any>()
            }
            @Suppress("UNCHECKED_CAST")
            current = current[key] as MutableMap<String, Any>
        }
        current[keys.last()] = value
        cache.clear()
    }

    fun remove(path: String): Any? {
        val keys = path.split(".")
        var current = configMap
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            if (current[key] is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                current = current[key] as MutableMap<String, Any>
            } else {
                return null
            }
        }
        cache.clear()
        return current.remove(keys.last())
    }

    fun update(map: Map<String, Any>) {
        configMap = map.toMutableMap()
        cache.clear()
    }

    fun save(file: File, template: List<String>?) {
        val yaml = Yaml()
        val output = yaml.dumpAsMap(configMap)
        file.parentFile.mkdirs()
        file.createNewFile()
        var updatedLines = mergeComments(output.lines().toMutableList(), lines.toList())
        if (template != null) {
            updatedLines = mergeComments(updatedLines, template)
        }
        file.writeText(updatedLines.joinToString("\n"))
    }

    // 合并注释
    private fun mergeComments(configLines: MutableList<String>, templateLines: List<String>): MutableList<String> {
        val result = mutableListOf<String>()
        var configIndex = 0
        val templateIndexMap = mutableMapOf<String, Int>()

        while (configIndex < configLines.size) {
            val configLine = configLines[configIndex].trim()
            if (configLine.isNotEmpty() && !configLine.startsWith("#")) {
                val configPath = getPath(configLines, configIndex)

                if (!hasCommentAbove(configLines, configIndex)) {
                    val templateIndex = findLineIndex(templateLines, configPath, templateIndexMap)
                    if (templateIndex != null && hasCommentAbove(templateLines, templateIndex)) {
                        val templateComment = collectCommentsAbove(templateLines, templateIndex)
                        result.addAll(templateComment)
                    }
                }
            }
            result.add(configLines[configIndex])
            configIndex++
        }

        return result
    }

    private fun getPath(lines: List<String>, index: Int): String {
        val path = mutableListOf<String>()
        var currentIndentation = getIndentation(lines[index])

        for (i in index downTo 0) {
            val line = lines[i].trim()
            if (line.isEmpty() || line.startsWith("#")) continue

            val indentation = getIndentation(lines[i])
            if (indentation < currentIndentation) {
                path.add(0, line.substringBefore(":").trim())
                currentIndentation = indentation
            }
        }
        path.add(lines[index].substringBefore(":").trim())
        return path.joinToString(".")
    }

    private fun getIndentation(line: String): Int {
        return line.indexOfFirst { !it.isWhitespace() }
    }

    private fun hasCommentAbove(lines: List<String>, index: Int): Boolean {
        if (index == 0) return false
        var i = index - 1
        while (i >= 0) {
            val line = lines[i].trim()
            if (line.isEmpty()) {
                i--
                continue
            }
            return line.startsWith("#")
        }
        return false
    }

    private fun collectCommentsAbove(lines: List<String>, index: Int): List<String> {
        val comments = mutableListOf<String>()
        var i = index - 1
        while (i >= 0) {
            val line = lines[i]
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                comments.add(0, line)
            } else {
                break
            }
            i--
        }
        return comments
    }

    private fun findLineIndex(lines: List<String>, path: String, indexMap: MutableMap<String, Int>): Int? {
        if (indexMap.containsKey(path)) return indexMap[path]
        for (i in lines.indices) {
            val line = lines[i].trim()
            if (line.isEmpty() || line.startsWith("#")) continue
            val currentPath = getPath(lines, i)
            if (currentPath == path) {
                indexMap[path] = i
                return i
            }
        }
        return null
    }
}