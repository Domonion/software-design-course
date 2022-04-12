package services

import ClientId
import events.ClientInside
import events.ClientOutside
import events.Event
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

class ReportService {
    private val daily = mutableMapOf<LocalDate, MutableList<Duration>>()
    private val presentAt = mutableMapOf<ClientId, Instant>()
    private var visits = 0
    private var duration = Duration.ZERO

    fun handle(id: ClientId, event: Event) {
        if (event is ClientInside) {
            presentAt[id] = event.cameAt
        } else if (event is ClientOutside) {
            val start = presentAt[id]!!
            val date = start.toLocalDateTime(TimeZone.UTC).date
            val visitDuration = event.exitedAt - start

            daily.compute(date) { _, previous -> (previous ?: mutableListOf()).apply { add(visitDuration) } }
            visits++
            duration += visitDuration
        }
    }

    fun meanVisitsPerDay(): Double {
        val days = daily.keys.size
        val res = daily.values.fold(0) { acc, visits -> acc + visits.size }

        return res.toDouble() / days
    }

    fun meanVisitDuration(): Duration {
        return duration / visits
    }

    fun dailyMeanVisitDuration(): Map<LocalDate, Duration> {
        return daily.mapValues { (_, list) -> list.fold(Duration.ZERO) { acc, duration -> acc + duration } }
    }
}