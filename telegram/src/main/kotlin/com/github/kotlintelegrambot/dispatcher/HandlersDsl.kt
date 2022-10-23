package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ChannelHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ContactHandler
import com.github.kotlintelegrambot.dispatcher.handlers.DiceHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.handlers.HandleAnimation
import com.github.kotlintelegrambot.dispatcher.handlers.HandleAudio
import com.github.kotlintelegrambot.dispatcher.handlers.HandleCallbackQuery
import com.github.kotlintelegrambot.dispatcher.handlers.HandleChannelPost
import com.github.kotlintelegrambot.dispatcher.handlers.HandleCommand
import com.github.kotlintelegrambot.dispatcher.handlers.HandleContact
import com.github.kotlintelegrambot.dispatcher.handlers.HandleDice
import com.github.kotlintelegrambot.dispatcher.handlers.HandleDocument
import com.github.kotlintelegrambot.dispatcher.handlers.HandleError
import com.github.kotlintelegrambot.dispatcher.handlers.HandleGame
import com.github.kotlintelegrambot.dispatcher.handlers.HandleInlineQuery
import com.github.kotlintelegrambot.dispatcher.handlers.HandleLocation
import com.github.kotlintelegrambot.dispatcher.handlers.HandleMessage
import com.github.kotlintelegrambot.dispatcher.handlers.HandleNewChatMembers
import com.github.kotlintelegrambot.dispatcher.handlers.HandlePhotos
import com.github.kotlintelegrambot.dispatcher.handlers.HandlePollAnswer
import com.github.kotlintelegrambot.dispatcher.handlers.HandlePreCheckoutQuery
import com.github.kotlintelegrambot.dispatcher.handlers.HandleSticker
import com.github.kotlintelegrambot.dispatcher.handlers.HandleText
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideo
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideoNote
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVoice
import com.github.kotlintelegrambot.dispatcher.handlers.InlineQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.LocationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.dispatcher.handlers.NewChatMembersHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PollAnswerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PreCheckoutQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AnimationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AudioHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.DocumentHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.GameHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.PhotosHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.StickerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoNoteHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VoiceHandler
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.extensions.filters.Filter.All

fun Dispatcher.message(handlerName: String? = null, chatId: Long? = null, body: HandleMessage) {
    addHandler(MessageHandler(All, body), handlerName, chatId)
}

fun Dispatcher.message(filter: Filter, handlerName: String? = null, chatId: Long? = null, body: HandleMessage) {
    addHandler(MessageHandler(filter, body), handlerName, chatId)
}

fun Dispatcher.command(command: String, handlerName: String = "/$command", chatId: Long? = null, body: HandleCommand) {
    addHandler(CommandHandler(command, body), handlerName, chatId)
}

fun Dispatcher.text(text: String? = null, handlerName: String? = text, chatId: Long? = null, body: HandleText) {
    addHandler(TextHandler(text, body), handlerName, chatId)
}

fun Dispatcher.callbackQuery(
    callbackData: String? = null,
    handlerName: String? = callbackData,
    chatId: Long? = null,
    body: HandleCallbackQuery
) {
    addHandler(
        CallbackQueryHandler(callbackData = callbackData, handleCallbackQuery = body),
        handlerName, chatId
    )
}

fun Dispatcher.callbackQuery(
    callbackData: String? = null,
    handlerName: String? = callbackData,
    chatId: Long? = null,
    callbackAnswerText: String? = null,
    callbackAnswerShowAlert: Boolean? = null,
    callbackAnswerUrl: String? = null,
    callbackAnswerCacheTime: Int? = null,
    body: HandleCallbackQuery
) {
    addHandler(
        CallbackQueryHandler(
            callbackData = callbackData,
            callbackAnswerText = callbackAnswerText,
            callbackAnswerShowAlert = callbackAnswerShowAlert,
            callbackAnswerUrl = callbackAnswerUrl,
            callbackAnswerCacheTime = callbackAnswerCacheTime,
            handleCallbackQuery = body
        ),
        handlerName, chatId
    )
}

fun Dispatcher.contact(handlerName: String? = null, chatId: Long? = null, body: HandleContact) {
    addHandler(ContactHandler(body), handlerName, chatId)
}

fun Dispatcher.location(handlerName: String? = null, chatId: Long? = null, body: HandleLocation) {
    addHandler(LocationHandler(body), handlerName, chatId)
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(ErrorHandler(body))
}

fun Dispatcher.preCheckoutQuery(handlerName: String? = null, chatId: Long? = null, body: HandlePreCheckoutQuery) {
    addHandler(PreCheckoutQueryHandler(body), handlerName, chatId)
}

fun Dispatcher.channel(handlerName: String? = null, chatId: Long? = null, body: HandleChannelPost) {
    addHandler(ChannelHandler(body), handlerName, chatId)
}

fun Dispatcher.inlineQuery(handlerName: String? = null, chatId: Long? = null, body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body), handlerName, chatId)
}

fun Dispatcher.audio(handlerName: String? = null, chatId: Long? = null, body: HandleAudio) {
    addHandler(AudioHandler(body), handlerName, chatId)
}

fun Dispatcher.document(handlerName: String? = null, chatId: Long? = null, body: HandleDocument) {
    addHandler(DocumentHandler(body), handlerName, chatId)
}

fun Dispatcher.animation(handlerName: String? = null, chatId: Long? = null, body: HandleAnimation) {
    addHandler(AnimationHandler(body), handlerName, chatId)
}

fun Dispatcher.game(handlerName: String? = null, chatId: Long? = null, body: HandleGame) {
    addHandler(GameHandler(body), handlerName, chatId)
}

fun Dispatcher.photos(handlerName: String? = null, chatId: Long? = null, body: HandlePhotos) {
    addHandler(PhotosHandler(body), handlerName, chatId)
}

fun Dispatcher.sticker(handlerName: String? = null, chatId: Long? = null, body: HandleSticker) {
    addHandler(StickerHandler(body), handlerName, chatId)
}

fun Dispatcher.video(handlerName: String? = null, chatId: Long? = null, body: HandleVideo) {
    addHandler(VideoHandler(body), handlerName, chatId)
}

fun Dispatcher.voice(handlerName: String? = null, chatId: Long? = null, body: HandleVoice) {
    addHandler(VoiceHandler(body), handlerName, chatId)
}

fun Dispatcher.videoNote(handlerName: String? = null, chatId: Long? = null, body: HandleVideoNote) {
    addHandler(VideoNoteHandler(body), handlerName, chatId)
}

fun Dispatcher.newChatMembers(handlerName: String? = null, chatId: Long? = null, body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body), handlerName, chatId)
}

fun Dispatcher.pollAnswer(handlerName: String? = null, chatId: Long? = null, body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body), handlerName, chatId)
}

fun Dispatcher.dice(handlerName: String? = null, chatId: Long? = null, body: HandleDice) {
    addHandler(DiceHandler(body), handlerName, chatId)
}
