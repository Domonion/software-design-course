package util

import kotlinx.coroutines.channels.Channel
import java.io.Closeable

class CloseableChannel<T>(private val channel: Channel<T> = Channel(10)) : Channel<T> by channel, Closeable {
    override fun close() {
        channel.close()
    }
}