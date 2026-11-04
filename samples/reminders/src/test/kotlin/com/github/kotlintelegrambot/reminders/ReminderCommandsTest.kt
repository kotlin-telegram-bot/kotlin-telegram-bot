package com.github.kotlintelegrambot.reminders

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ReminderCommandsTest {
    private val scheduler = FakeScheduler()
    private val service =
        ReminderService(
            Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneId.of("UTC")),
            ZoneId.of("UTC"),
            scheduler,
        ) { _, _ -> }
    private val commands = ReminderCommands(service)

    @Test
    fun `preview displays parsed title full local time timezone and confirmation command`() {
        val reply = commands.handle(1, "private", "remind", "Call *Alice* tomorrow at 7pm")

        assertTrue(reply.contains("Call *Alice*"))
        assertTrue(reply.contains("2026-11-02"))
        assertTrue(reply.contains("19:00:00"))
        assertTrue(reply.contains("UTC"))
        assertTrue(reply.contains("/confirm"))
        assertTrue(scheduler.tasks.isEmpty())
    }

    @Test
    fun `date only preview visibly includes inherited reference clock`() {
        val reply = commands.handle(1, "private", "remind", "Call Alice tomorrow")

        assertTrue(reply.contains("2026-11-02"))
        assertTrue(reply.contains("10:00:00"))
    }

    @Test
    fun `group commands cannot create confirm or cancel private reminders`() {
        commands.handle(1, "private", "remind", "Call Alice tomorrow at 7pm")
        listOf("remind", "confirm", "cancel").forEach { command ->
            assertTrue(commands.handle(1, "group", command, "Call Bob tomorrow at 8pm").contains("private chat"))
        }
        commands.handle(1, "private", "confirm")

        assertEquals(1, scheduler.tasks.size)
        commands.handle(1, "private", "cancel")
        assertTrue(scheduler.tasks.single().cancelled)
    }
}
