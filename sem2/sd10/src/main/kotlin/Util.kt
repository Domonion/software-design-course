import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.serialization.json.Json
import rx.Observable

const val port = 8081
const val id = "id"
const val currency = "currency"
const val price = "price"

val prettyJson = Json { prettyPrint = true }

typealias QueryParameters = MutableMap<String, MutableList<String>>

fun success() = ProcessResult(HttpResponseStatus.OK, Observable.just("success"))
fun incorrect() = ProcessResult(HttpResponseStatus.BAD_REQUEST, Observable.just("incorrect request"))
fun error(e: Exception) = ProcessResult(
    HttpResponseStatus.INTERNAL_SERVER_ERROR,
    Observable.just("exception while execution:\n${e.message}\n${e.stackTraceToString()}")
)

fun error(msg: String) = ProcessResult(
    HttpResponseStatus.INTERNAL_SERVER_ERROR,
    Observable.just(msg)
)

