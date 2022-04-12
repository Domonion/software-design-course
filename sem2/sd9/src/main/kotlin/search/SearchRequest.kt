package search

import util.QueryString
import kotlin.time.Duration

data class SearchRequest(val request: QueryString, val timeout: Duration)