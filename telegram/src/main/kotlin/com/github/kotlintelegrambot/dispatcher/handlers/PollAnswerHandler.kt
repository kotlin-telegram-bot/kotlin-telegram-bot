package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.polls.PollAnswer

data class PollAnswerHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val pollAnswer: PollAnswer
) : HandlerEnvironment(bot, update)

internal class PollAnswerHandler(
    private val handle: HandlePollAnswer
) : Handler() {

    override val groupIdentifier: String
        get() = "pollAnswer"

    override fun checkUpdate(update: Update): Boolean = update.pollAnswer != null

    override fun invoke(bot: Bot, update: Update) {
        val pollAnswer = update.pollAnswer
        checkNotNull(pollAnswer)
        handle.invoke(PollAnswerHandlerEnvironment(bot, update, pollAnswer))
    }
}
