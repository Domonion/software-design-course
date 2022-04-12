package api

import kotlinx.serialization.Serializable

@Serializable
data class ApiQuery(val queryName: String, val parameters: Map<String, String>)