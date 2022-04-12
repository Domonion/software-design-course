@kotlinx.serialization.Serializable
data class User(val id: Int, val currency: CURRENCY) {
    companion object {
        fun parseUser(query: QueryParameters): User {
            val id = query.getValue(id).single().toInt()
            val currencyString = query.getValue(currency).single().uppercase()
            val currency = CURRENCY.valueOf(currencyString)

            return User(id, currency)
        }
    }
}