package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message

public interface WithMessageHandlerEnvironment : UpdateHandlerEnvironment {
    public val message: Message
}

public val WithMessageHandlerEnvironment.chatId: ChatId
    get() = ChatId.fromId(message.chat.id)
