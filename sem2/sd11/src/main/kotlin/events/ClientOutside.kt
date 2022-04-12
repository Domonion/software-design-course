package events

import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
class ClientOutside(val exitedAt: Instant) : Event()
