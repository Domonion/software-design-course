import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.HttpStatusCode
import search.QueryResult
import search.SearchEngine
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration

class StubServer(port: Int, timeout: Duration, responsePath: String, searchEngine: SearchEngine) : Closeable {
    private val stubServer = ClientAndServer.startClientAndServer(port)
    private val responses = Json.decodeFromString<Responses>(Files.readString(Path.of(responsePath)))

    init {
        stubServer.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/")
        ).respond { request: HttpRequest ->
            val timeoutMillis = timeout.inWholeMilliseconds

            if (timeoutMillis > 0) {
                Thread.sleep(timeoutMillis)
            }

            val query = request.getFirstQueryStringParameter("request")
            val sites = responses.answers.getOrDefault(query, emptyList())
            val result = QueryResult(searchEngine, sites)
            val responseBody = Json.encodeToString(result)

            HttpResponse.response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withBody(responseBody)
        }
    }

    override fun close() {
        stubServer.close()
    }
}