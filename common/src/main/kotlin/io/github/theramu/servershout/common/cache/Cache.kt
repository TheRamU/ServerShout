package io.github.theramu.servershout.common.cache

/**
 * @author TheRamU
 * @since 2024/8/30 1:50
 */
class Cache<K : Any, V>(
    private val expiration: Long = 1000 * 60 * 60,
    private val expirationRule: ExpirationRule = ExpirationRule.TIME_TO_IDLE,
    private val maxSize: Int = Int.MAX_VALUE,
    private val pruneInterval: Long = 1000 * 60 * 5
) : MutableMap<K, V> {
    private val cache = HashMap<K, CacheEntry<V>>()
    private var lastPruneTime = System.currentTimeMillis()

    init {
        require(expiration > 0) { "Expiration time must be positive" }
        require(maxSize > 0) { "Max size must be positive" }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            prune()
            return cache.entries.map { (key, value) ->
                object : MutableMap.MutableEntry<K, V> {
                    override val key: K get() = key
                    override val value: V get() = value.value
                    override fun setValue(newValue: V): V {
                        cache[key] = CacheEntry(newValue, value.creationTime, System.currentTimeMillis())
                        return newValue
                    }
                }
            }.toMutableSet()
        }

    override val keys: MutableSet<K>
        get() {
            prune()
            return cache.keys.toMutableSet()
        }

    override val size: Int
        get() {
            prune()
            return cache.size
        }

    override val values: MutableCollection<V>
        get() {
            prune()
            return cache.values.map { it.value }.toMutableList()
        }

    override fun put(key: K, value: V): V? {
        prune()
        val currentTime = System.currentTimeMillis()
        val previous = cache.put(key, CacheEntry(value, currentTime, currentTime))?.value
        if (cache.size > maxSize) {
            evictOldest()
        }
        return previous
    }

    override fun get(key: K): V? {
        prune(key)
        val entry = cache[key] ?: return null
        entry.lastAccessTime = System.currentTimeMillis()
        return entry.value
    }

    override fun clear() {
        return cache.clear()
    }

    override fun isEmpty(): Boolean {
        prune()
        return cache.isEmpty()
    }

    override fun remove(key: K): V? {
        prune()
        return cache.remove(key)?.value
    }

    override fun putAll(from: Map<out K, V>) {
        prune()
        val currentTime = System.currentTimeMillis()
        from.forEach { (key, value) ->
            cache[key] = CacheEntry(value, currentTime, currentTime)
        }
        if (cache.size > maxSize) {
            evictOldest()
        }
    }

    override fun containsValue(value: V): Boolean {
        prune()
        return cache.values.any { it.value == value }
    }

    override fun containsKey(key: K): Boolean {
        prune(key)
        return cache.containsKey(key)
    }

    private fun prune(key: K) {
        cache[key]?.let { entry ->
            if (isExpired(entry)) {
                cache.remove(key)
            }
        }
        prune()
    }

    private fun prune() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPruneTime < pruneInterval) {
            return
        }
        lastPruneTime = currentTime
        cache.entries.removeIf { (_, entry) -> isExpired(entry) }
    }

    private fun isExpired(entry: CacheEntry<V>): Boolean {
        val currentTime = System.currentTimeMillis()
        return when (expirationRule) {
            ExpirationRule.TIME_TO_LIVE -> currentTime - entry.creationTime > expiration
            ExpirationRule.TIME_TO_IDLE -> currentTime - entry.lastAccessTime > expiration
        }
    }

    private fun evictOldest() {
        val oldest = cache.entries.minByOrNull { it.value.lastAccessTime }?.key
        oldest?.let { cache.remove(it) }
    }

    enum class ExpirationRule {
        TIME_TO_LIVE,
        TIME_TO_IDLE
    }

    private data class CacheEntry<V>(
        val value: V,
        val creationTime: Long,
        var lastAccessTime: Long
    )
}