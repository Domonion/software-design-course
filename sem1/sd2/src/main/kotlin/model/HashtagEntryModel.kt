package model

import kotlinx.serialization.json.JsonObject

class HashtagEntryModel(private val json: JsonObject) {
    fun id() = json["id"]!!
    fun date() = json["date"]!!
}