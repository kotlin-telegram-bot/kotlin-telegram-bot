package com.github.kotlintelegrambot.reminders

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ReminderServiceTest {
    private val clock = MutableClock(Instant.parse("2026-11-01T10:00:00Z"))
    private val scheduler = FakeScheduler()
    private val deliveries = mutableListOf<Pair<Long, Reminder>>()
    private val service = ReminderService(clock, ZoneOffset.UTC, scheduler) { chat, reminder -> deliveries.add(chat to reminder) }

    @ParameterizedTest
    @ValueSource(strings = ["Call Alice tomorrow at 7pm", "Позвонить Алисе завтра в 19:00"])
    fun `English and Russian resolve to the same literal instant`(text: String) {
        val result = service.draft(1, text)

        assertEquals(ReplyKind.PREVIEW, result.kind)
        assertEquals(Instant.parse("2026-11-02T19:00:00Z"), result.reminder!!.at)
        assertTrue(scheduler.tasks.isEmpty())
        assertTrue(deliveries.isEmpty())
    }

    @Test
    fun `date only retains the reference clock and parsed title`() {
        val result = service.draft(1, "Call Alice tomorrow")

        assertEquals(ReplyKind.PREVIEW, result.kind)
        assertEquals(Instant.parse("2026-11-02T10:00:00Z"), result.reminder!!.at)
        assertEquals("Call Alice", result.reminder.title)
    }

    @Test
    fun `configured timezone controls parsing`() {
        val localService = ReminderService(clock, ZoneId.of("Asia/Tokyo"), scheduler) { _, _ -> }

        val result = localService.draft(1, "Call Alice tomorrow at 7pm")

        assertEquals(Instant.parse("2026-11-02T10:00:00Z"), result.reminder!!.at)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Call Alice tomorrow at 7",
            "Call Alice tomorrow at 25:99",
            "Call Alice tomorrow at 24:00",
            "Call Alice tomorrow at 13pm",
            "Call Alice on 03/04/2027",
            "Позвонить Алисе завтра в 25:99",
        ],
    )
    fun `ambiguous or invalid clock fragments never become date only reminders`(text: String) {
        assertFalse(service.draft(1, text).kind == ReplyKind.PREVIEW)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        assertTrue(scheduler.tasks.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["Call Alice tomorrow and Friday", "Позвонить Алисе завтра и в пятницу"])
    fun `multiple matches are rejected`(text: String) {
        val reply = service.draft(1, text)
        assertEquals(ReplyKind.AMBIGUOUS, reply.kind, reply.toString())
    }

    @ParameterizedTest
    @ValueSource(strings = ["Call Alice tomorrow from 7pm to 8pm", "Call Alice every day", "Позвонить Алисе каждый день", "Позвонить Алисе завтра с 19:00 до 20:00"])
    fun `ranges and recurrence are rejected`(text: String) {
        assertEquals(ReplyKind.UNSUPPORTED, service.draft(1, text).kind)
        assertTrue(scheduler.tasks.isEmpty())
    }

    @Test
    fun `no date blank title and past times are rejected`() {
        assertEquals(ReplyKind.NO_DATE, service.draft(1, "Call Alice").kind)
        assertEquals(ReplyKind.NO_TITLE, service.draft(1, "tomorrow at 7pm").kind)
        assertEquals(ReplyKind.PAST, service.draft(1, "Call Alice yesterday at 7pm").kind)
        assertEquals(ReplyKind.PAST, service.draft(1, "Call Alice today at 10:00").kind)
    }

    @Test
    fun `confirmation schedules only once and delivery consumes scheduled state`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        assertEquals(ReplyKind.SCHEDULED, service.confirm(1).kind)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        assertEquals(1, scheduler.tasks.size)
        assertEquals(Instant.parse("2026-11-02T19:00:00Z"), scheduler.tasks.single().at)
        assertTrue(deliveries.isEmpty())

        scheduler.fire(0)
        scheduler.fire(0)

        assertEquals(1, deliveries.size)
        assertEquals(1L, deliveries.single().first)
        assertEquals("Call Alice", deliveries.single().second.title)
        assertEquals(ReplyKind.NOTHING_TO_CANCEL, service.cancel(1).kind)
    }

    @Test
    fun `late confirmation rechecks the future boundary`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        clock.now = Instant.parse("2026-11-02T19:00:00Z")

        assertEquals(ReplyKind.EXPIRED, service.confirm(1).kind)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        assertTrue(scheduler.tasks.isEmpty())
    }

    @Test
    fun `another chat cannot confirm or cancel a reminder`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(2).kind)
        assertEquals(ReplyKind.NOTHING_TO_CANCEL, service.cancel(2).kind)
        assertEquals(ReplyKind.SCHEDULED, service.confirm(1).kind)
        scheduler.fire(0)

        assertEquals(listOf(1L), deliveries.map { it.first })
    }

    @Test
    fun `cancel removes a draft and ignores a racing cancelled callback`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        assertEquals(ReplyKind.CANCELLED, service.cancel(1).kind)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        service.draft(1, "Call Bob tomorrow at 8pm")
        service.confirm(1)

        assertEquals(ReplyKind.CANCELLED, service.cancel(1).kind)
        assertTrue(scheduler.tasks.single().cancelled)
        service.draft(1, "Call Carol tomorrow at 9pm")
        service.confirm(1)
        scheduler.fire(0)
        assertTrue(deliveries.isEmpty())
        scheduler.fire(1)
        assertEquals("Call Carol", deliveries.single().second.title)
    }

    @Test
    fun `one draft and one scheduled reminder coexist without replacing scheduled work`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        service.confirm(1)
        assertEquals(ReplyKind.PREVIEW, service.draft(1, "Call Bob tomorrow at 8pm").kind)
        assertEquals(ReplyKind.ALREADY_SCHEDULED, service.confirm(1).kind)
        assertEquals(1, scheduler.tasks.size)
        scheduler.fire(0)
        assertEquals(ReplyKind.SCHEDULED, service.confirm(1).kind)
        scheduler.fire(1)
        assertEquals(listOf("Call Alice", "Call Bob"), deliveries.map { it.second.title })
    }

    @Test
    fun `invalid replacement clears the old draft`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        service.draft(1, "Call Bob tomorrow at 7")

        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
    }

    @Test
    fun `bounded capacity counts drafts and scheduled reminders and frees on cancellation`() {
        val limited = ReminderService(clock, ZoneOffset.UTC, scheduler, capacity = 2) { _, _ -> }
        limited.draft(1, "Call Alice tomorrow at 7pm")
        limited.confirm(1)
        limited.draft(1, "Call Bob tomorrow at 8pm")
        assertEquals(ReplyKind.CAPACITY, limited.draft(2, "Call Carol tomorrow at 9pm").kind)
        assertEquals(ReplyKind.PREVIEW, limited.draft(1, "Call Dan tomorrow at 9pm").kind)
        limited.cancel(1)
        assertEquals(ReplyKind.PREVIEW, limited.draft(2, "Call Carol tomorrow at 9pm").kind)
    }

    @Test
    fun `expired drafts and completed work release capacity`() {
        val limited = ReminderService(clock, ZoneOffset.UTC, scheduler, capacity = 1) { _, _ -> }
        limited.draft(1, "Call Alice tomorrow at 7pm")
        clock.now = Instant.parse("2026-11-02T20:00:00Z")
        assertEquals(ReplyKind.PREVIEW, limited.draft(2, "Call Bob tomorrow at 7pm").kind)
        limited.confirm(2)
        scheduler.fire(0)
        assertEquals(ReplyKind.PREVIEW, limited.draft(3, "Call Carol tomorrow at 7pm").kind)
    }

    @Test
    fun `scheduler rejection leaves no false scheduled reminder`() {
        val unavailable = ReminderService(clock, ZoneOffset.UTC, ReminderScheduler { _, _ -> throw IllegalStateException("closed") }) { _, _ -> }
        unavailable.draft(1, "Call Alice tomorrow at 7pm")

        assertEquals(ReplyKind.UNAVAILABLE, unavailable.confirm(1).kind)
        assertEquals(ReplyKind.CANCELLED, unavailable.cancel(1).kind)
    }

    @Test
    fun `oversized input is rejected without retaining a draft`() {
        assertEquals(ReplyKind.TOO_LONG, service.draft(1, "a".repeat(2001) + " tomorrow").kind)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Call Alice daily at 5pm",
            "Call Alice weekly at 5pm",
            "Call Alice tomorrow between 5 and 8",
            "Позвонить Алисе завтра между 5 и 8",
            "Call Alice tomorrow at 17:30 EST",
            "Call Alice tomorrow at 7pm America/New_York",
        ],
    )
    fun `unparsed recurrence ranges and embedded zones are rejected`(text: String) {
        assertFalse(service.draft(1, text).kind == ReplyKind.PREVIEW)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        assertTrue(scheduler.tasks.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Call Alice tomorrow at 7pm (UTC)",
            "Call Alice tomorrow at 7pm(EST)",
            "Call Alice tomorrow at 19:00 [Europe/Berlin]",
            "Call Alice tomorrow at 19:00[UTC+02:00]",
            "Call Alice tomorrow at 7pm (America/New_York)",
            "Call Alice tomorrow at 7pm[UTC]",
        ],
    )
    fun `wrapped timezones never produce a confirmable draft`(text: String) {
        service.draft(1, "Call Bob tomorrow at 8pm")

        assertEquals(ReplyKind.UNSUPPORTED, service.draft(1, text).kind)
        assertEquals(ReplyKind.NO_DRAFT, service.confirm(1).kind)
        assertTrue(scheduler.tasks.isEmpty())
    }

    @Test
    fun `ordinary parenthesized title text remains allowed`() {
        val reply = service.draft(1, "Call Alice tomorrow at 7pm (office)")

        assertEquals(ReplyKind.PREVIEW, reply.kind)
        assertEquals(Instant.parse("2026-11-02T19:00:00Z"), reply.reminder!!.at)
    }

    @Test
    fun `a frequency word in the title is not automatically a recurrence`() {
        val reply = service.draft(1, "Read daily report tomorrow at 7pm")

        assertEquals(ReplyKind.PREVIEW, reply.kind)
        assertEquals("Read daily report", reply.reminder!!.title)
    }

    @Test
    fun `concurrent confirmations and callbacks still deliver once`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        val pool = Executors.newFixedThreadPool(2)
        try {
            val start = CyclicBarrier(2)
            val replies =
                pool
                    .invokeAll(
                        List(2) {
                            Callable {
                                start.await(5, TimeUnit.SECONDS)
                                service.confirm(1).kind
                            }
                        },
                    ).map { it.get(5, TimeUnit.SECONDS) }
            assertEquals(1, replies.count { it == ReplyKind.SCHEDULED })
            assertEquals(1, scheduler.tasks.size)
            val fire = CyclicBarrier(2)
            pool
                .invokeAll(
                    List(2) {
                        Callable {
                            fire.await(5, TimeUnit.SECONDS)
                            scheduler.fire(0)
                        }
                    },
                ).forEach { it.get(5, TimeUnit.SECONDS) }
            assertEquals(1, deliveries.size)
        } finally {
            pool.shutdownNow()
        }
    }

    @Test
    fun `a slow delivery does not hold the service lock`() {
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val slow =
            ReminderService(clock, ZoneOffset.UTC, scheduler) { _, _ ->
                started.countDown()
                check(release.await(5, TimeUnit.SECONDS))
            }
        slow.draft(1, "Call Alice tomorrow at 7pm")
        slow.confirm(1)
        val pool = Executors.newFixedThreadPool(2)
        try {
            val delivery = pool.submit { scheduler.fire(0) }
            assertTrue(started.await(5, TimeUnit.SECONDS))
            val reply = pool.submit<ReminderReply> { slow.draft(2, "Call Bob tomorrow at 8pm") }
            assertEquals(ReplyKind.PREVIEW, reply.get(2, TimeUnit.SECONDS).kind)
            release.countDown()
            delivery.get(5, TimeUnit.SECONDS)
        } finally {
            release.countDown()
            pool.shutdownNow()
        }
    }

    @Test
    fun `closing cancels pending tasks and suppresses stale callbacks`() {
        service.draft(1, "Call Alice tomorrow at 7pm")
        service.confirm(1)

        service.close()
        scheduler.fire(0)

        assertTrue(scheduler.tasks.single().cancelled)
        assertTrue(deliveries.isEmpty())
        assertEquals(ReplyKind.UNAVAILABLE, service.draft(1, "Call Alice tomorrow at 7pm").kind)
    }
}

internal class MutableClock(
    var now: Instant,
) : Clock() {
    override fun instant(): Instant = now

    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId): Clock = Clock.fixed(now, zone)
}

internal class FakeScheduler : ReminderScheduler {
    data class Task(
        val at: Instant,
        val action: () -> Unit,
        var cancelled: Boolean = false,
    )

    val tasks = mutableListOf<Task>()

    override fun schedule(
        at: Instant,
        action: () -> Unit,
    ): Cancellation {
        val task = Task(at, action)
        tasks.add(task)
        return Cancellation { task.cancelled = true }
    }

    // Deliberately invoke cancelled and repeated callbacks to exercise service ownership checks.
    fun fire(index: Int) = tasks[index].action()
}
