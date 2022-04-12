import io.reactivex.netty.protocol.http.server.HttpServer

// jvm & kotlin compile target - 1.8. otherwise NCDFE
//https://play.kotlinlang.org/hands-on/Full%20Stack%20Web%20App%20with%20Kotlin%20Multiplatform/06_Adding_Persistence
//http://mongodb.github.io/mongo-java-driver-rx/1.5/getting-started/quick-tour/
//https://github.com/jershell/kbson
fun main() =
    HttpServer
        .newServer(port)
        .start { httpRequest, httpResponse ->
            val path = httpRequest.decodedPath
            val query = httpRequest.queryParameters
            val (responseStatus, response) = process(path, query)

            httpResponse.status = responseStatus
            httpResponse.writeString(response)
        }
        .awaitShutdown()

private fun process(path: String, query: QueryParameters): ProcessResult {
    return try {
        val endpoint = ENDPOINTS.decide(path)

        endpoint.execute(query)
    } catch (e: Exception) {
        error(e)
    }
}