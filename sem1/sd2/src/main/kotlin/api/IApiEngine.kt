package api

import kotlinx.serialization.json.JsonElement

interface IApiEngine {
    suspend fun send(apiQuery: ApiQuery): JsonElement
}