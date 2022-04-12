package search

import util.QueryString
import java.net.URI
import java.net.http.HttpRequest

enum class SearchEngine {
    GOOGLE {
        override val port = 8081
    },
    YANDEX {
        override val port = 8082
    },
    BING {
        override val port = 8083
    };

    public abstract val port: Int

    fun createRequest(request: QueryString): HttpRequest {
        val wrapped = "http://localhost:$port?request=$request"
        val uri = URI.create(wrapped)
        val httpRequest = HttpRequest.newBuilder().uri(uri).build()

        return httpRequest
    }
}