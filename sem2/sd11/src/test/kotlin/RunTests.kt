@file:OptIn(ExperimentalTime::class)

import events.EventStorage
import services.ManagerService
import services.ReportService
import services.TurnstileService
import kotlinx.datetime.*
import model.Client
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

val Instant.date
    get() = this.toLocalDateTime(TimeZone.UTC).date

class RunTests {
    private lateinit var now: Instant
    private lateinit var clock: MyClock
    private lateinit var eventStorage: EventStorage
    private lateinit var manager: ManagerService

    @BeforeEach
    fun init() {
        now = Clock.System.now()
        clock = MyClock(now)
        eventStorage = EventStorage()
        manager = ManagerService(eventStorage, clock)
    }

    @Test
    fun clientBuildTest() {
        manager.createClient(1, "Somebody", now, now)
        manager.extendMembership(1, 30.days)

        val account = manager.getClient(1)

        Assertions.assertEquals("Somebody", account.name)
        Assertions.assertEquals(now, account.createdAt)
        Assertions.assertEquals(now, account.membershipStartsAt)
        Assertions.assertEquals(now + 30.days, account.membershipExpiresAt)
        Assertions.assertFalse(account.isInside)
        Assertions.assertEquals(Client.empty.lastVisit, account.lastVisit)
    }

    @Test
    fun statisticsTest() {
        val turnstile = TurnstileService(eventStorage, clock)
        val report = ReportService()

        manager.createClient(1, "Somwbody", now, now + 30.days)

        turnstile.comeInside(1)
        clock += 2.hours
        turnstile.exit(1)

        clock += 24.hours

        turnstile.comeInside(1)
        clock += 1.hours
        turnstile.exit(1)

        eventStorage.subscribe(report)

        Assertions.assertEquals(1.0, report.meanVisitsPerDay(), 1e-8)
        Assertions.assertEquals(90.minutes, report.meanVisitDuration())

        val daily = report.dailyMeanVisitDuration()
        val today = now.date
        val tomorrow = (now + 1.days).date

        Assertions.assertEquals(daily[today], 2.hours)
        Assertions.assertEquals(daily[tomorrow], 1.hours)
    }
}