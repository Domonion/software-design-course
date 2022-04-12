import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.serialization.encodeToString
import rx.Observable

private val registerKeys = setOf(id, currency)
private val addKeys = setOf(id, currency, price)
private val listKeys = setOf(currency)

enum class ENDPOINTS {
    REGISTER {
        override val keys = registerKeys
        override val endpoint = "/register"
        override fun doExecute(query: QueryParameters): ProcessResult {
            val user = User.parseUser(query)

            return if (Driver.register(user)) success()
            else error("could not add user to db")
        }
    },
    ADD {
        override val keys = addKeys
        override val endpoint = "/add"
        override fun doExecute(query: QueryParameters): ProcessResult {
            val product = Product.parseProduct(query)

            return if (Driver.add(product)) success()
            else error("could not add product to db")
        }
    },
    LIST {
        override val keys = listKeys
        override val endpoint = "/list"
        override fun doExecute(query: QueryParameters): ProcessResult {
            val originalProducts = Driver.products()
            val currencyString = query.getValue(currency).single().uppercase()
            val currency = CURRENCY.valueOf(currencyString)
            val resultProducts = originalProducts.map { it.copy(currency = currency, price = currency.convert(it.currency, it.price)) }

            return ProcessResult(HttpResponseStatus.OK, Observable.just(prettyJson.encodeToString(resultProducts)))
        }
    },
    UNKNOWN {
        override val keys = setOf("unknown")
        override val endpoint = "unknown"
        override fun doExecute(query: QueryParameters) = incorrect()
    };

    protected abstract val keys: Set<String>
    protected abstract val endpoint: String

    fun execute(query: QueryParameters): ProcessResult {
        if (!check(query))
            return incorrect()

        return doExecute(query)
    }

    abstract fun doExecute(query: QueryParameters): ProcessResult

    fun check(query: QueryParameters): Boolean {
        return keys == query.keys
    }

    companion object {
        fun decide(path: String): ENDPOINTS {
            return values().find { it.endpoint == path } ?: UNKNOWN
        }
    }
}