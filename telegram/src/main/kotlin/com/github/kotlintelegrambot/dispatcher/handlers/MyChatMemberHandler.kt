package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.Update

data class MyChatMemberHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val myChatMember: ChatMemberUpdated
)

internal class MyChatMemberHandler(
    private val handleMyChatMember: HandleMyChatMember
) : Handler {

    override fun checkUpdate(update: Update) = update.myChatMember != null

    override fun handleUpdate(bot: Bot, update: Update)
    {
        val myChatMember = update.myChatMember
        checkNotNull(myChatMember)

        val chatMemberEnv = MyChatMemberHandlerEnvironment(bot, update, myChatMember)
        handleMyChatMember(chatMemberEnv)
    }
}
