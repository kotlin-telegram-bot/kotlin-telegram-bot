package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandlePollAnswer
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class PollAnswerHandler(handlePollAnswer: HandlePollAnswer) : Handler(HandlePollAnswerProxy(handlePollAnswer)) {

    override val groupIdentifier: String
        get() = "pollAnswer"

    override fun checkUpdate(update: Update): Boolean = update.pollAnswer != null
}

class HandlePollAnswerProxy(
    private val handlePollAnswer: HandlePollAnswer
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        val pollAnswer = update.pollAnswer
        checkNotNull(pollAnswer)
        handlePollAnswer.invoke(bot, pollAnswer)
    }
}
