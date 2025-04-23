package com.github.kotlintelegrambot.dispatcher.router

import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.HandleMessage
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.extensions.filters.Filter
import org.junit.jupiter.api.Test

class RouterTest {
    val handlerCall: HandleMessage = {}

    @Test
    fun `router handler`() {
        val router = router {}
        check(router.getHandlers().isEmpty())

        val handler = MessageHandler(Filter.All, handlerCall)

        router.addHandler(handler)
        check(router.getHandlers().size == 1)

        router.removeHandler(handler)
        check(router.getHandlers().isEmpty())
    }

    @Test
    fun `router error handler`() {
        val router = router {}
        val handler = ErrorHandler {}

        router.addErrorHandler(handler)
        check(router.getErrorHandlers().size == 1)

        router.removeErrorHandler(handler)
        check(router.getErrorHandlers().isEmpty())
    }

    @Test
    fun `child router`() {
        val router = router {}
        check(router.getChildren().isEmpty())

        val childRouter = router {}

        router.includeRouter(childRouter)
        check(router.getChildren().size == 1)

        router.excludeRouter(childRouter)
        check(router.getHandlers().isEmpty())
    }
}
