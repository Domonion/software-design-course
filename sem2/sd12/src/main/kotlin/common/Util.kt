package common

import AppException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

const val listenHost = "0.0.0.0"
const val sendHost = "127.0.0.1"
const val serverPort = 8081
const val clientPort = 8082
const val listenServerUrl = "http://$listenHost:$serverPort"
const val sendToServerUrl = "http://$sendHost:$serverPort"
const val listenClientUrl = "http://$listenHost:$clientPort"
const val sendToClientUrl = "http://$sendHost:$clientPort"

object Common {
    fun <T> Routing.executeResponse(path: String, execute: suspend PipelineContext<Unit, ApplicationCall>.() -> T) {
        get(path) {
            try {
                call.respondText(status = HttpStatusCode.OK, text = this.execute().toString())
            } catch (e: AppException) {
                call.respondText(status = HttpStatusCode.BadRequest, text = e.message)
            }
        }
    }

    fun Routing.executeEmpty(path: String, execute: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) {
        executeResponse(path) {
            execute()

            return@executeResponse ""
        }
    }

    fun ApplicationCall.extractInt(param: String): Int {
        val value = parameters[param]?.toIntOrNull() ?: -1

        if (value < 0)
            throw AppException("Incorrect $param, must be non-negative")

        return value
    }
}