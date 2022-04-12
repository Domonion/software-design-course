package actors

import akka.actor.AbstractActor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import search.QueryResult
import search.SearchEngine
import util.QueryString
import java.net.http.HttpClient

abstract class SearchEngineActor : AbstractActor() {
    abstract val searchEngine: SearchEngine

    private fun getTop15Answers(request: String) {
        val httpClient = HttpClient.newHttpClient()
        val httpRequest = searchEngine.createRequest(request)
        val responseBuilder = java.net.http.HttpResponse.BodyHandlers.ofString()
        val responseString = httpClient.send(httpRequest, responseBuilder).body()
        val response = Json.decodeFromString<QueryResult>(responseString)

        sender().tell(response, self)
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(QueryString::class.java, ::getTop15Answers)
            .build()
    }
}