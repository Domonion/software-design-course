import api.ApiQuery
import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action.contentType
import com.xebialabs.restito.semantics.Action.stringContent
import com.xebialabs.restito.semantics.Condition.*
import com.xebialabs.restito.server.StubServer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.glassfish.grizzly.http.Method
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import vk.IVkCredentials
import vk.VkEngine
import java.util.regex.Pattern

class StubServerTests {
    @Test
    fun simpleVkEngineTest(): Unit = runBlocking {
        val creds = Mockito.mock(IVkCredentials::class.java)
        val port = 5001
        val accessToken = "111"
        val apiVersion = "222"

        whenever(creds.apiUrl).thenReturn("http://localhost:$port/")
        whenever(creds.apiVersion).thenReturn(apiVersion)
        whenever(creds.accessToken).thenReturn(accessToken)

        val server = StubServer(port).run()
        val queryName = "/a"
        val stringJson = "{ \"result\" : 1 }"
        val json = Json.parseToJsonElement(stringJson)
        val apiQuery = ApiQuery(queryName, mapOf("b" to "c"))

        whenHttp(server)
            .match(
                method(Method.GET),
                startsWithUri(queryName),
                parameter("access_token", accessToken),
                parameter("v", apiVersion),
                parameter("b", "c")
            )
            .then(stringContent(stringJson), contentType("application/json"))

        val engine = VkEngine(creds)
        val result = engine.send(apiQuery)

        Assertions.assertEquals(json, result)
        verify(creds).apiUrl
        verify(creds).accessToken
        verify(creds).apiVersion

        server.stop()
    }
}