package services

import model.Client
import events.ClientCreated
import ClientId
import events.ClientMembershipExtended
import events.EventStorage
import buildClient
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.DurationUnit

class ManagerService(private val storage: EventStorage, private val clock: Clock) {
    fun getClient(id: ClientId): Client = buildClient(storage, id)

    fun createClient(
        id: ClientId, name: String,
        membershipStartsAt: Instant,
        membershipExpiresAt: Instant
    ) {
        val client = buildClient(storage, id)

        if (client == Client.empty) {
            storage.addEvent(id, ClientCreated(name, clock.now(), membershipStartsAt, membershipExpiresAt))
        }
    }

    fun extendMembership(id: ClientId, duration: Duration) {
        storage.addEvent(id, ClientMembershipExtended(duration.toInt(DurationUnit.DAYS)))
    }
}