package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.Update

data class MyChatMemberHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val myChatMember: ChatMemberUpdated,
)

class MyChatMemberHandler(
    private val handleMyChatMember: HandleMyChatMember,
) : Handler {
    override fun checkUpdate(update: Update): Boolean = update.myChatMember != null
    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.myChatMember)
        handleMyChatMember(MyChatMemberHandlerEnvironment(bot, update, update.myChatMember))
    }
}
