@kotlinx.serialization.Serializable
data class Product(val id: Int, val currency: CURRENCY, val price: Double) {
    companion object {
        fun parseProduct(query: QueryParameters): Product {
            val id = query.getValue(id).single().toInt()
            val currencyString = query.getValue(currency).single().uppercase()
            val currency = CURRENCY.valueOf(currencyString)
            val price = query.getValue(price).single().toDouble()

            return Product(id, currency, price)
        }
    }
}