package events

import ClientId
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ClientInfo
import prettyJson
import services.ReportService
import java.nio.file.Files
import java.nio.file.Path

private val dbPath = Path.of("db.json")

class EventStorage {
    init {
        Files.writeString(dbPath, Json.encodeToString(mutableMapOf<ClientId, ClientInfo>()))
    }

    private val subscribers = mutableListOf<ReportService>()

    fun events(): MutableMap<ClientId, ClientInfo> {
        val db = Files.readString(dbPath)
        val parsed = prettyJson.decodeFromString<MutableMap<ClientId, ClientInfo>>(db)

        return parsed
    }

    private fun commitEvents(db: Map<ClientId, ClientInfo>) {
        val encoded = prettyJson.encodeToString(db)

        Files.writeString(dbPath, encoded)
    }

    fun addEvent(id: ClientId, event: Event) {
        val currentEvents = events()

        currentEvents.compute(id) { key, was -> (was ?: ClientInfo(key)).apply { events.add(event) } }
        commitEvents(currentEvents)
        subscribers.forEach { it.handle(id, event) }
    }

    operator fun get(id: ClientId) = events()[id]

    fun subscribe(subscriber: ReportService) {
        for ((id, info) in events()) {
            for (event in info.events) {
                subscriber.handle(id, event)
            }
        }

        subscribers.add(subscriber)
    }
}
