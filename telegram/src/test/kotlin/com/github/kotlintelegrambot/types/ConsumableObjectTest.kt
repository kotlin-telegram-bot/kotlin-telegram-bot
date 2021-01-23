package com.github.kotlintelegrambot.types

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConsumableObjectTest {

    @Test
    fun `test that consumbale object cannot be consumed twice`() {
        val consumableObject = object : ConsumableObject() {}

        assertFalse(consumableObject.consumed)
        consumableObject.consume()
        assertTrue(consumableObject.consumed)
        assertThrows<IllegalStateException> {
            consumableObject.consume()
        }
    }
}
