@kotlinx.serialization.Serializable
data class UserInfo(val id: Int, val portfolio: MutableMap<String, Int>, val funds: Int)