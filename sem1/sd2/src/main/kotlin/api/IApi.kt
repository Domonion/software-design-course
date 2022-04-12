package api

import kotlinx.serialization.json.JsonElement

interface IApi {
    suspend fun query(query: StandardQuery): JsonElement
}