import common.serverPort
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.FixedHostPortGenericContainer

class ClientIntegrationTests {
    companion object {
        @ClassRule
        @JvmStatic
        fun simpleWebServer() = FixedHostPortGenericContainer("exchange-app:1.0")
            .withFixedExposedPort(serverPort, serverPort)
            .withExposedPorts(serverPort)
    }

    private lateinit var testClient: HttpClient

    @Before
    fun init() {
        testClient = HttpClient(Apache) {
            expectSuccess = false
        }
    }

    @Test
    fun integrationTest(): Unit = runBlocking {
        Client().startClient(testClient)

        testClient.register(0)
        testClient.fund(0, 100500)
        testClient.add("APL", 123)

        assertEquals(testClient.stockInfo("APL"), StockInfo(100, 123, "APL"))

        testClient.purchase(0, 99, "APL")
        testClient.add("MSFT", 12)

        assertEquals(testClient.stockInfo("MSFT"), StockInfo(100, 12, "MSFT"))
        assertEquals(testClient.total(0), 9900)
        assertEquals(testClient.portfolio(0), mapOf("APL" to 99))

        testClient.purchase(0, 2, "MSFT")
        testClient.change("APL", 333)
        testClient.fund(0, 11)

        assertEquals(testClient.total(0), 33167)
        assertEquals(testClient.portfolio(0), mapOf("APL" to 99, "MSFT" to 2))

        testClient.sell(0, 98, "APL")
        testClient.change("MSFT", 600)

        assertEquals(testClient.total(0), 1533)
        assertEquals(testClient.portfolio(0), mapOf("APL" to 1, "MSFT" to 2))

        testClient.register(1)
        testClient.register(2)
        testClient.fund(1, 1234)
        testClient.fund(2, 54321)
        testClient.purchase(1, 1, "APL")

        assertThrows(RequestException::class.java) { runBlocking { testClient.purchase(2, 100500, "MSFT") } }
        assertThrows(RequestException::class.java) { runBlocking { testClient.stockInfo("MSFW") } }

        assertEquals(testClient.info("APL"), StockInfo(333, 23, "APL"))
        assertEquals(testClient.info("MSFT"), StockInfo(600, 10, "MSFT"))
        assertEquals(testClient.total(1), 333)
        assertEquals(testClient.total(2), 0)
        assertEquals(testClient.portfolio(1), mapOf("APL" to 1))
        assertEquals(testClient.portfolio(2), emptyMap<String, Int>())
    }
}