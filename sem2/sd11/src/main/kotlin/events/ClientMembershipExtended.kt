package events

@kotlinx.serialization.Serializable
data class ClientMembershipExtended(val durationInDays: Int) : Event()