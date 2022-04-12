package services

import ClientId
import events.ClientInside
import events.ClientOutside
import events.EventStorage
import buildClient
import kotlinx.datetime.*

class TurnstileService(private val storage: EventStorage, private val clock: Clock) {
    fun comeInside(id: ClientId) {
        val now = clock.now()

        if (canComeInside(id, now)) {
            val event = ClientInside(now)

            storage.addEvent(id, event)
        }
    }

    fun exit(id: ClientId) {
        if (isInside(id)) {
            val now = clock.now()
            val event = ClientOutside(now)

            storage.addEvent(id, event)
        }
    }

    private fun canComeInside(id: ClientId, now: Instant): Boolean {
        val client = buildClient(storage, id)

        return client.membershipStartsAt <= now && now < client.membershipExpiresAt
    }

    private fun isInside(id: ClientId): Boolean {
        val client = buildClient(storage, id)

        return client.isInside
    }
}
