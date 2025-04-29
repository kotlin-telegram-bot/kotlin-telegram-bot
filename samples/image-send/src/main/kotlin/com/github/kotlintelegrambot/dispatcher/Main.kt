package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.logging.LogLevel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*

fun main() {
    val httpClient = HttpClient(CIO)
    val bot = bot {
        token = "YOUR_API_KEY"
        timeout = 30
        logLevel = LogLevel.Network.Headers

        dispatch {
            command("image") {
                val response = httpClient.get("https://avatars.githubusercontent.com/u/57418018")
                response.bodyAsChannel().toInputStream().use {
                    bot.sendPhoto(ChatId.fromId(message.chat.id), TelegramFile.ByInputStream(it, contentLength = response.contentLength() ?: -1))
                }
            }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }

    bot.startPolling()
}
