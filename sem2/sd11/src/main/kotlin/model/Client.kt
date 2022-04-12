package model

import ClientId
import future
import kotlinx.datetime.Instant
import past

@kotlinx.serialization.Serializable
data class Client(
    val id: ClientId,
    val name: String,
    val createdAt: Instant,
    val membershipStartsAt: Instant,
    val membershipExpiresAt: Instant,
    val lastVisit: Instant?,
    val isInside: Boolean
) {
    companion object {
        val empty = Client(
            id = -1,
            name = "impossible",
            createdAt = past,
            membershipStartsAt = future,
            membershipExpiresAt = future,
            lastVisit = past,
            isInside = false
        )
    }
}