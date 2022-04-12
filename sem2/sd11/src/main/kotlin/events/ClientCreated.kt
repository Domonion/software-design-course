package events

import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
data class ClientCreated(
    val name: String,
    val createdAt: Instant,
    val membershipStartsAt: Instant,
    val membershipExpiresAt: Instant
) : Event()