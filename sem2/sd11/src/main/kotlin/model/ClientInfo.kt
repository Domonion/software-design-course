package model

import ClientId
import events.Event

@kotlinx.serialization.Serializable
data class ClientInfo(val id: ClientId, val events: MutableList<Event> = mutableListOf())