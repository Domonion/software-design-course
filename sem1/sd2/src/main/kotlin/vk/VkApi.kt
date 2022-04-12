package vk

import api.StandardQuery
import kotlinx.serialization.json.JsonElement

class VkApi(private val translator: IVkQueryTranslator, private val engine: IVkEngine) : IVkApi {
    override suspend fun query(query: StandardQuery): JsonElement {
        return translator.translateQuery(query).let { engine.send(it) }
    }
}