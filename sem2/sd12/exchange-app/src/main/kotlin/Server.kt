import common.Common.executeEmpty
import common.Common.executeResponse
import common.Common.extractInt
import common.listenHost
import common.serverPort
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val exchange = ConcurrentHashMap<String, StockInfo>()

private fun ApplicationCall.extractStock(shouldExist: Boolean = true): String {
    val stock = parameters["stock"]

    if (stock == null || (shouldExist && !exchange.containsKey(stock))) throw AppException("Incorrect stock")

    return stock
}

fun main() {
    Server().startServer()
}

class Server {
    fun startServer() {
        embeddedServer(Jetty, host = listenHost, port = serverPort) {
            install(ShutDownUrl.ApplicationCallFeature) {
                shutDownUrl = "/shutdown"
                exitCodeSupplier = { 0 }
            }
            install(StatusPages)

            routing {
                get("/test") {
                    println("origin: ${call.request.origin.remoteHost}")
                    println("local: ${call.request.local.remoteHost}")
                    call.respondText(status = HttpStatusCode.OK, text = "i am working")
                }

                executeEmpty("/add") {
                    val stock = call.extractStock(shouldExist = false)
                    val diff = call.extractInt("amount")

                    exchange.compute(stock) { _, possibleValue ->
                        (possibleValue ?: StockInfo(100, 0, stock)).let { it.copy(amount = it.amount + diff) }
                    }
                }

                executeResponse("/info") {
                    val stock = call.extractStock()

                    Json.encodeToString(exchange[stock])
                }

                executeEmpty("/sell") {
                    val stock = call.extractStock()
                    val diff = call.extractInt("amount")
                    val buyPrice = call.extractInt("price")

                    exchange.computeIfPresent(stock) { _, value ->
                        if (value.amount < diff) {
                            throw AppException("not enough stock")
                        }

                        if (value.price != buyPrice) {
                            throw AppException("price changed")
                        }

                        value.copy(amount = value.amount - diff)
                    }
                }

                executeEmpty("/change") {
                    val stock = call.extractStock()
                    val newPrice = call.extractInt("price")

                    exchange.computeIfPresent(stock) { _, value -> value.copy(price = newPrice) }!!.price
                }
            }
        }.start(wait = true)
    }
}