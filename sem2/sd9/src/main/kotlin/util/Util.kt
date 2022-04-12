package util

import akka.actor.ActorRef
import akka.actor.ActorRefFactory
import akka.actor.Props
import actors.MasterActor
import search.QueryResult
import search.SearchRequest
import kotlinx.coroutines.channels.Channel
import org.reflections.Reflections
import java.io.Closeable
import kotlin.time.toJavaDuration

typealias QueryString = String

const val packageName = "actors"
val reflections = Reflections(packageName)

suspend fun <T, R> T.useWith(another: () -> R, execute: suspend (T, R) -> Unit) where T : Closeable, R : Closeable =
    useWith(another(), execute)

suspend fun <T, R> T.useWith(another: R, execute: suspend (T, R) -> Unit) where T : Closeable, R : Closeable {
    this.use { t ->
        another.use { r ->
            execute(t, r)
        }
    }
}

fun groupUse(vararg list: Closeable, execute: () -> Unit) {
    if (list.isEmpty()) {
        execute()
        return
    }

    if (list.size == 1) {
        list.first().use {
            execute()
        }
        return
    }

    val kek = list.toList()

    kek.first().use {
        groupUse(*kek.subList(1, kek.size).toTypedArray(), execute = execute)
    }
}

suspend fun runSearchWith(request: SearchRequest, consume: suspend (Channel<QueryResult>) -> Unit) {
    CloseableChannel<QueryResult>().useWith(::CloseableActorSystem) { channel, actorSystem ->
        executeSearch(request, channel, actorSystem)

        consume(channel)
    }
}

suspend fun executeSearch(request: SearchRequest, channel: Channel<QueryResult>, actorSystem: ActorRefFactory) {
    val master = actorSystem.actorOf(Props.create(MasterActor::class.java, channel, request.timeout.toJavaDuration()), "master")

    master.tell(request, ActorRef.noSender())
    channel.invokeOnClose { actorSystem.stop(master) }
}