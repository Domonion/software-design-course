package events

import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
data class ClientInside(val cameAt: Instant) : Event()
