package com.github.kotlintelegrambot.polls

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.pollAnswer
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.polls.PollType.QUIZ

fun main() {
    bot {
        token = "BOT_API_TOKEN"
        dispatch {
            pollAnswer {
                println("${pollAnswer.user.username} has selected the option ${pollAnswer.optionIds.lastOrNull()} in the poll ${pollAnswer.pollId}")
            }
            command("regularPoll") {
                bot.sendPoll(
                    chatId = ChatId.fromId(message.chat.id),
                    question = "Pizza with or without pineapple?",
                    options = listOf("With :(", "Without :)"),
                    isAnonymous = false
                )
            }

            command("quizPoll") {
                bot.sendPoll(
                    chatId = ChatId.fromId(message.chat.id),
                    type = QUIZ,
                    question = "Java or Kotlin?",
                    options = listOf("Java", "Kotlin"),
                    correctOptionId = 1,
                    isAnonymous = false
                )
            }

            command("closedPoll") {
                bot.sendPoll(
                    chatId = ChatId.fromId(message.chat.id),
                    type = QUIZ,
                    question = "Foo or Bar?",
                    options = listOf("Foo", "Bar", "FooBar"),
                    correctOptionId = 1,
                    isClosed = true,
                    explanation = "A closed quiz because I can"
                )
            }
        }
    }.startPolling()
}
