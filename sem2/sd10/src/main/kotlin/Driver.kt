import com.github.jershell.kbson.KBson
import com.github.jershell.kbson.toDocument
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import java.util.concurrent.TimeUnit

const val database = "hw10"
const val users = "users"
const val products = "products"
const val timeout = 1000L
val timeunit = TimeUnit.MILLISECONDS

val kBson = KBson()

@ExperimentalSerializationApi
object Driver {
    private val client = MongoClients.create()

    fun register(user: User): Boolean {
        val bsonDoc = kBson.stringify(User.serializer(), user).toDocument()

        val result = client
            .getDatabase(database)
            .getCollection(users)
            .insertOne(bsonDoc)
            .timeout(timeout, timeunit)
            .toBlocking()
            .single()

        return result == Success.SUCCESS
    }

    fun add(product: Product): Boolean {
        val bsonDoc = kBson.stringify(Product.serializer(), product).toDocument()

        val result = client
            .getDatabase(database)
            .getCollection(products)
            .insertOne(bsonDoc)
            .timeout(timeout, timeunit)
            .toBlocking()
            .single()

        return result == Success.SUCCESS
    }

    fun products(): List<Product> {
        return client
            .getDatabase(database)
            .getCollection(products)
            .find()
            .toObservable()
            .map {
                prettyJson.decodeFromString<Product>(it.toJson())
            }
            .toBlocking()
            .toIterable()
            .toList()
    }
}