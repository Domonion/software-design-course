import common.sendToClientUrl
import common.sendToServerUrl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

data class RequestException(override val message: String) : Exception()

fun HttpResponse.assertStatusCode(code: HttpStatusCode = HttpStatusCode.OK): HttpResponse {
    if (status != code) {
        throw RequestException("request status: $status")
    }

    return this
}

suspend fun HttpClient.register(id: Int) {
    get<HttpResponse>("$sendToClientUrl/register?id=$id").assertStatusCode()
}

suspend fun HttpClient.fund(id: Int, amount: Int) {
    get<HttpResponse>("$sendToClientUrl/fund?id=$id&amount=$amount").assertStatusCode()
}

suspend fun HttpClient.total(id: Int): Int {
    val response = get<HttpResponse>("$sendToClientUrl/portfolio/total?id=$id").assertStatusCode()

    return response.readText().toIntOrNull() ?: throw RequestException("total is not integer")
}

suspend fun HttpClient.portfolio(id: Int): Map<String, Int> {
    val response = get<HttpResponse>("$sendToClientUrl/portfolio/list?id=$id").assertStatusCode()
    val text = response.readText()

    try {
        return Json.decodeFromString(text)
    } catch (e: Exception) {
        throw RequestException("could not decode portfolio, text: $text")
    }
}

suspend fun HttpClient.purchase(id: Int, amount: Int, stock: String) {
    get<HttpResponse>("$sendToClientUrl/purchase?id=$id&amount=$amount&stock=$stock").assertStatusCode()
}

suspend fun HttpClient.sell(id: Int, amount: Int, stock: String) {
    get<HttpResponse>("$sendToClientUrl/sell?id=$id&amount=$amount&stock=$stock").assertStatusCode()
}

suspend fun HttpClient.stockInfo(stock: String): StockInfo {
    val text = get<HttpResponse>("$sendToServerUrl/info?stock=$stock").assertStatusCode().readText()

    return Json.decodeFromString(text)
}

suspend fun HttpClient.add(stock: String, diff: Int) {
    get<HttpResponse>("$sendToServerUrl/add?stock=$stock&amount=$diff").assertStatusCode()
}

suspend fun HttpClient.change(stock: String, price: Int) {
    get<HttpResponse>("$sendToServerUrl/change?stock=$stock&price=$price").assertStatusCode()
}