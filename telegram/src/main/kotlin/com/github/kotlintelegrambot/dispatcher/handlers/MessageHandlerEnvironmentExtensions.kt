package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.entities.ChatId

fun IMessageHandlerEnvironment.replyToMessage(text: String) {
    bot.sendMessage(ChatId.fromId(message.chat.id), text, replyToMessageId = message.messageId)
}
