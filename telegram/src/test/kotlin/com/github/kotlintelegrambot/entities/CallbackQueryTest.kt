package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CallbackQueryTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun parseGameCallbackWithoutData() {
        val query =
            gson.fromJson(
                """
                {
                    "id": "callback-1",
                    "from": {"id": 42, "is_bot": false, "first_name": "Tester"},
                    "chat_instance": "chat-1",
                    "game_short_name": "chess"
                }
                """.trimIndent(),
                CallbackQuery::class.java,
            )

        assertNull(query.data)
        assertEquals("chess", query.gameShortName)
    }

    @Test
    fun parseDataCallbackWithoutGameName() {
        val query =
            gson.fromJson(
                """
                {
                    "id": "callback-2",
                    "from": {"id": 42, "is_bot": false, "first_name": "Tester"},
                    "chat_instance": "chat-1",
                    "data": "move:e2-e4"
                }
                """.trimIndent(),
                CallbackQuery::class.java,
            )

        assertEquals("move:e2-e4", query.data)
        assertNull(query.gameShortName)
    }
}
