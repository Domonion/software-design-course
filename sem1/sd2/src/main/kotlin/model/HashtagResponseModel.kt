package model

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class HashtagResponseModel(private val jsonElement: JsonElement) {
    fun entries(): Iterable<HashtagEntryModel> = jsonElement.jsonObject["response"]!!.jsonObject["items"]!!.jsonArray.map { HashtagEntryModel(it.jsonObject) }
}