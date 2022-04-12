import java.io.Closeable

class LruCache<T>(private val size: Int) {
    private data class CacheItem<T>(var prev: CacheItem<T>?, var next: CacheItem<T>?, val value: T?)

    private val valuesMap = HashMap<T, CacheItem<T>>()
    private var currentCount = 0
    private var head: CacheItem<T> = CacheItem(null, null, null)

    init {
        head.next = head
        head.prev = head
    }

    val recentlyUsed: T
        get() {
            assert(head.next !== head)

            return head.next!!.value!!
        }

    fun use(value: T) {
        ensureCapacity().use {
            valuesMap.computeIfAbsent(value) {
                ++currentCount
                CacheItem(head, head.next, value)
            }.also {
                head.next!!.prev = it
                head.next = it
            }
        }
    }

    private fun ensureCapacity(): Closeable {
        assert(currentCount <= size)

        return Closeable {
            if (currentCount > size) {
                head.prev = head.prev!!.prev
                head.prev!!.next = head
                currentCount--;
            }

            assert(currentCount <= size)
        }
    }
}