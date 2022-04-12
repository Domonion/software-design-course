package actors

import akka.actor.*
import akka.japi.pf.DeciderBuilder
import search.QueryResult
import search.SearchRequest
import util.reflections
import kotlinx.coroutines.channels.Channel
import java.net.ConnectException
import java.time.Duration

class MasterActor(private val channel: Channel<QueryResult>, timeout: Duration) : AbstractActor() {
    init {
        context.receiveTimeout = timeout
    }

    private val results = mutableListOf<QueryResult>()
    private var children: Int = 0

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(SearchRequest::class.java, ::ask)
            .match(QueryResult::class.java) {
                results.add(it)
                tryReturn()
            }
            .match(ReceiveTimeout::class.java) {
                doReturn()
            }
            .build()
    }

    private fun tryReturn() {
        if (results.size == children) {
            doReturn()
        }
    }

    private fun doReturn() {
        results.forEach(channel::trySend)
        channel.close()
    }

    override fun supervisorStrategy(): SupervisorStrategy {
        return OneForOneStrategy(
            false,
            DeciderBuilder.match(ConnectException::class.java) { OneForOneStrategy.stop() }
                .build()
        )
    }

    private fun ask(request: SearchRequest) {
        val subtypes = reflections.getSubTypesOf(SearchEngineActor::class.java)

        children = subtypes.size
        subtypes.forEach {
            context.actorOf(Props.create(it), "child${it.simpleName}").tell(request.request, self)
        }
    }
}