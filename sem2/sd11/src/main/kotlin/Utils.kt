import events.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import model.Client
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

typealias ClientId = Int

val past = Instant.DISTANT_PAST
val future = Instant.DISTANT_FUTURE
val prettyJson = Json { prettyPrint = true }

fun buildClient(storage: EventStorage, id: ClientId): Client {
    val clientInfo = storage[id] ?: return Client.empty

    val createdEvent = (clientInfo.events.first() as ClientCreated)
    val (name, createdAt, membershipStartAt, memberShipExpiresAt) = createdEvent
    val extended =
        clientInfo.events.filterIsInstance<ClientMembershipExtended>().map { it.durationInDays.days }
            .fold(Duration.ZERO) { acc, new -> acc + new }
    val lastVisit = clientInfo.events.filterIsInstance<ClientInside>().lastOrNull()?.cameAt ?: past
    val isInside = clientInfo.events.lastOrNull { it is ClientInside || it is ClientOutside } is ClientInside

    return Client(id, name, createdAt, membershipStartAt, memberShipExpiresAt + extended, lastVisit, isInside)
}