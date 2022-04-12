package controller

import api.IApi
import api.StandardQuery
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonPrimitive
import model.HashtagResponseModel
import java.time.Duration
import java.time.Instant

class HashtagController(private val api: IApi) : IController {
    fun countHashtag(hashtag: String, hoursRange: Duration): List<Int> {
        assert(hashtag.startsWith("#"))

        val hashtagQuery = StandardQuery("hashtag", mapOf("q" to hashtag))
        val hashtagJson = runBlocking { api.query(hashtagQuery) }
        val hashtagResponseModel = HashtagResponseModel(hashtagJson)

        return buildDistribution(hashtagResponseModel, hoursRange)
    }

    private fun buildDistribution(hashtagModel: HashtagResponseModel, hoursRange: Duration): List<Int> {
        val now = Instant.now()
        val start = now.minusSeconds(hoursRange.seconds)
        val hoursCount = hoursRange.toHours().toInt()
        val distribution = MutableList(hoursCount) { 0 }

        for (entry in hashtagModel.entries()) {
            val unixDateTime = entry.date().jsonPrimitive.content.toLong()
            val entryTime = Instant.ofEpochSecond(unixDateTime)

            if (entryTime.isBefore(start))
                continue

            val hourIndex = (entryTime.epochSecond - start.epochSecond) / 3600

            distribution[hourIndex.toInt()] += 1
        }

        return distribution
    }
}