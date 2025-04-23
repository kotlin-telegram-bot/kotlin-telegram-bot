package com.github.kotlintelehrambot.router

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.router.command
import com.github.kotlintelegrambot.dispatcher.router.message
import com.github.kotlintelegrambot.dispatcher.router.photos
import com.github.kotlintelegrambot.dispatcher.router.router
import com.github.kotlintelegrambot.dispatcher.router.video
import com.github.kotlintelegrambot.entities.ChatId

val commandRouter = router {
    command("start") {
        bot.sendMessage(ChatId.fromId(message.chat.id), "Hello")
    }
}

val mediaRouter = router {
    photos {
        bot.sendMessage(
            ChatId.fromId(message.chat.id),
            "Photo",
        )
    }
    video {
        bot.sendMessage(
            ChatId.fromId(message.chat.id),
            "Video",
        )
    }
}

val routerWithChild = router {
    // Router can have own children
    includeRouter(commandRouter)
    includeRouter(mediaRouter)
}

fun main() {
    bot {
        dispatch {
            // Include routers to this dispatcher
            // Child router handlers will be run only after processing all handlers of the dispatcher
            includeRouter(veryLongRouter)
            includeRouter(routerWithChild)

            // This handler will run before any children router handlers
            message {}
        }
    }
}
