package vk

import api.ApiQuery
import io.ktor.client.*
import kotlinx.serialization.json.JsonElement
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import org.apache.http.client.utils.URIBuilder
import java.net.URL

class VkEngine(private val vkCredentials: IVkCredentials) : IVkEngine {
    private val ktorClient = HttpClient(CIO) {
        install(JsonFeature)
    }

    override suspend fun send(apiQuery: ApiQuery): JsonElement {
        val base = URL(vkCredentials.apiUrl)
        val resolved = URL(base, apiQuery.queryName)

        URIBuilder(resolved.toURI()).apply {
            apiQuery.parameters.forEach { (a, b) -> addParameter(a, b) }
            addParameter("access_token", vkCredentials.accessToken)
            addParameter("v", vkCredentials.apiVersion)
        }.let {
            return ktorClient.get(it.toString())
        }
    }
}