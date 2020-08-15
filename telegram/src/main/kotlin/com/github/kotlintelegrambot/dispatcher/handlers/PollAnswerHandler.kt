package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.polls.PollAnswer

data class PollAnswerHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val pollAnswer: PollAnswer
)

internal class PollAnswerHandler(
    handlePollAnswer: HandlePollAnswer
) : Handler(HandlePollAnswerProxy(handlePollAnswer)) {

    override val groupIdentifier: String
        get() = "pollAnswer"

    override fun checkUpdate(update: Update): Boolean = update.pollAnswer != null
}

private class HandlePollAnswerProxy(
    private val handlePollAnswer: HandlePollAnswer
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        val pollAnswer = update.pollAnswer
        checkNotNull(pollAnswer)

        val pollAnswerHandlerEnv = PollAnswerHandlerEnvironment(bot, update, pollAnswer)
        handlePollAnswer.invoke(pollAnswerHandlerEnv)
    }
}
