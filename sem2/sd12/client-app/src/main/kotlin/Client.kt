import common.Common.executeEmpty
import common.Common.executeResponse
import common.Common.extractInt
import common.clientPort
import common.listenHost
import common.sendToServerUrl
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.getValue
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sum

private val users = ConcurrentHashMap<Int, UserInfo>()

private fun ApplicationCall.extractId(shouldExist: Boolean = true): Int {
    val idParameter = parameters["id"]
    val idValue = idParameter?.toIntOrNull() ?: -1

    if (idParameter == null) {
        throw AppException("No id parameter")
    }

    if (idValue >= 0 && !shouldExist && users.containsKey(idValue)) throw AppException("id $idValue already exists")

    if (idValue < 0 && shouldExist && !users.containsKey(idValue)) {
        throw AppException("id $idValue not exists")
    }

    return idValue
}

suspend fun HttpClient.info(stock: String): StockInfo {
    val response: HttpResponse = get("$sendToServerUrl/info?stock=$stock")

    if (response.status != HttpStatusCode.OK) {
        throw AppException("cannot get stock $stock info")
    }

    return Json.decodeFromString(response.readText())
}

fun HttpClient.sell(stock: String, amount: Int, price: Int): Boolean = runBlocking {
    val response: HttpResponse = get("$sendToServerUrl/sell?stock=$stock&amount=$amount&price=$price")

    return@runBlocking response.status == HttpStatusCode.OK
}

fun main() {
    HttpClient(Apache) { expectSuccess = false }.use {
        Client().startClient(it, shouldWait = true)
    }
}

class Client {
    fun startClient(exchange: HttpClient, shouldWait: Boolean = false) {
        embeddedServer(Jetty, host = listenHost, port = clientPort) {
            install(ShutDownUrl.ApplicationCallFeature) {
                shutDownUrl = "/shutdown"
                exitCodeSupplier = { 0 }
            }
            install(StatusPages)

            routing {
                executeEmpty("/register") {
                    val id = call.extractId(shouldExist = false)

                    users.computeIfAbsent(id) { UserInfo(id, mutableMapOf(), 0) }
                }

                executeEmpty("/fund") {
                    val id = call.extractId()
                    val amount = call.extractInt("amount")

                    users.computeIfPresent(id) { _, value -> value.copy(funds = value.funds + amount) }
                }

                executeResponse("/portfolio/total") {
                    val id = call.extractId()

                    users.getValue(id).portfolio.map { (stock, amount) -> exchange.info(stock).price * amount }.sum()
                }

                executeResponse("/portfolio/list") {
                    val id = call.extractId()

                    Json.encodeToString(users.getValue(id).portfolio as Map<String, Int>)
                }

                executeEmpty("/purchase") {
                    val id = call.extractId()
                    val requestAmount = call.extractInt("amount")
                    val stock = call.parameters["stock"] ?: throw AppException("no stock parameter")
                    var done = false

                    while (!done) {
                        val stockInfo = exchange.info(stock)
                        val cost = stockInfo.price * requestAmount

                        if (stockInfo.amount < requestAmount) {
                            throw AppException("not enough stock amount")
                        }

                        users.compute(id) { _, userInfo ->
                            if (userInfo!!.funds < cost) {
                                throw AppException("not enough fund")
                            }

                            if (exchange.sell(stock, requestAmount, stockInfo.price)) {
                                done = true
                                userInfo.portfolio.compute(stock) { _, previous ->
                                    (previous ?: 0) + requestAmount
                                }
                                userInfo.copy(funds = userInfo.funds - cost)
                            } else {
                                userInfo
                            }
                        }
                    }
                }

                executeEmpty("/sell") {
                    val id = call.extractId()
                    val requestAmount = call.extractInt("amount")
                    val stock = call.parameters["stock"] ?: throw AppException("no stock parameter")

                    users.computeIfPresent(id) { _, value ->
                        if (!value.portfolio.contains(stock)) {
                            throw AppException("no stock $stock in portfolio")
                        }

                        val userAmount = value.portfolio.getValue(stock)

                        if (userAmount < requestAmount) {
                            throw AppException("not enough stock $stock to sell")
                        }

                        value.portfolio[stock] = value.portfolio[stock]!! - requestAmount
                        value
                    }
                }
            }
        }.start(wait = shouldWait)
    }
}