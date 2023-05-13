package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.Update

data class MyChatMemberHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chatMemberUpdated: ChatMemberUpdated,
)

internal class MyChatMemberHandler(
    private val chatType: String? = null,
    private val handleMyChatMember: HandleMyChatMember,
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        return update.myChatMember != null && (chatType == null || update.myChatMember.chat.type == chatType)
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.myChatMember)
        val myChatMemberHandlerEnv = MyChatMemberHandlerEnvironment(bot, update, update.myChatMember)
        handleMyChatMember(myChatMemberHandlerEnv)
    }
}
