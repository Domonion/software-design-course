package util

import akka.actor.ActorRefFactory
import akka.actor.ActorSystem
import java.io.Closeable

class CloseableActorSystem(private val actorSystem: ActorSystem = ActorSystem.create()) :
    ActorRefFactory by actorSystem, Closeable {
    override fun close() {
        actorSystem.terminate()
    }
}