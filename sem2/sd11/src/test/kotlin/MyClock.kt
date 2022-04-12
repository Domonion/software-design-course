import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class MyClock(private var now: Instant) : Clock {
    operator fun plusAssign(duration: Duration) {
        now += duration
    }

    override fun now() = now
}