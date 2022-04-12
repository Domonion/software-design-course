package search

import kotlinx.serialization.Serializable

@Serializable
data class QueryResult(val from: SearchEngine, val sites: List<String>)