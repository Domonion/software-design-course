import org.junit.jupiter.api.Test

class LruCacheTests {
    @Test
    fun simpleTest(){
        val cache = LruCache<Int>(2)
        cache.use(1)
        cache.use(2)
        cache.use(3)
        assert(cache.recentlyUsed == 3)
    }

    @Test
    fun stressMaxTest(){
        val cache = LruCache<Int>(100)

        for (i in 1..10000)
            cache.use(i)

        assert(cache.recentlyUsed == 10000)
    }

    @Test
    fun stressWidthTest(){
        val cache = LruCache<Int>(100)

        for (i in 1..10000)
            cache.use(i % 100)

        assert(cache.recentlyUsed == 0)
    }
}