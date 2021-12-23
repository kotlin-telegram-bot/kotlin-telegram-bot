package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.Companion
import com.github.kotlintelegrambot.entities.Update

public interface UpdateHandlerEnvironment : HandlerEnvironment {
    public val update: Update
}

public fun UpdateHandlerEnvironment.requireChatId(): ChatId =
    ChatId.fromId(requireNotNull(update.message).chat.id)

public val UpdateHandlerEnvironment.chatId: ChatId?
    get() = update.message?.let { Companion.fromId(it.chat.id) }
