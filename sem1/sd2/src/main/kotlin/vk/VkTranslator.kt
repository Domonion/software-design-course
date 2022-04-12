package vk

import api.ApiQuery
import api.StandardQuery

class VkTranslator : IVkQueryTranslator {
    override fun translateQuery(query: StandardQuery): ApiQuery {
        if(query.queryName != "hashtag")
            throw IllegalArgumentException("Only hashtag query supported")

        return ApiQuery("newsfeed.search",
            // simple deep-copy
            HashMap(query.parameters))
    }
}