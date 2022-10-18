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

fun Dispatcher.message(handlerName: String? = null, body: HandleMessage) {
    addHandler(MessageHandler(All, body), handlerName)
}

fun Dispatcher.message(filter: Filter, handlerName: String? = null, body: HandleMessage) {
    addHandler(MessageHandler(filter, body), handlerName)
}

fun Dispatcher.command(command: String, handlerName: String = "/$command", body: HandleCommand) {
    addHandler(CommandHandler(command, body), handlerName)
}

fun Dispatcher.text(text: String?, handlerName: String? = text, body: HandleText) {
    addHandler(TextHandler(text, body), handlerName)
}

fun Dispatcher.callbackQuery(
    callbackData: String? = null,
    handlerName: String? = callbackData,
    body: HandleCallbackQuery
) {
    addHandler(
        CallbackQueryHandler(callbackData = callbackData, handleCallbackQuery = body),
        handlerName
    )
}

fun Dispatcher.callbackQuery(
    callbackData: String? = null,
    handlerName: String? = callbackData,
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
        ), handlerName
    )
}

fun Dispatcher.contact(handlerName: String? = null, body: HandleContact) {
    addHandler(ContactHandler(body), handlerName)
}

fun Dispatcher.location(handlerName: String? = null, body: HandleLocation) {
    addHandler(LocationHandler(body), handlerName)
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(ErrorHandler(body))
}

fun Dispatcher.preCheckoutQuery(handlerName: String? = null, body: HandlePreCheckoutQuery) {
    addHandler(PreCheckoutQueryHandler(body), handlerName)
}

fun Dispatcher.channel(handlerName: String? = null, body: HandleChannelPost) {
    addHandler(ChannelHandler(body), handlerName)
}

fun Dispatcher.inlineQuery(handlerName: String? = null, body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body), handlerName)
}

fun Dispatcher.audio(handlerName: String? = null, body: HandleAudio) {
    addHandler(AudioHandler(body), handlerName)
}

fun Dispatcher.document(handlerName: String? = null, body: HandleDocument) {
    addHandler(DocumentHandler(body), handlerName)
}

fun Dispatcher.animation(handlerName: String? = null, body: HandleAnimation) {
    addHandler(AnimationHandler(body), handlerName)
}

fun Dispatcher.game(handlerName: String? = null, body: HandleGame) {
    addHandler(GameHandler(body), handlerName)
}

fun Dispatcher.photos(handlerName: String? = null, body: HandlePhotos) {
    addHandler(PhotosHandler(body), handlerName)
}

fun Dispatcher.sticker(handlerName: String? = null, body: HandleSticker) {
    addHandler(StickerHandler(body), handlerName)
}

fun Dispatcher.video(handlerName: String? = null, body: HandleVideo) {
    addHandler(VideoHandler(body), handlerName)
}

fun Dispatcher.voice(handlerName: String? = null, body: HandleVoice) {
    addHandler(VoiceHandler(body), handlerName)
}

fun Dispatcher.videoNote(handlerName: String? = null, body: HandleVideoNote) {
    addHandler(VideoNoteHandler(body), handlerName)
}

fun Dispatcher.newChatMembers(handlerName: String? = null, body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body), handlerName)
}

fun Dispatcher.pollAnswer(handlerName: String? = null, body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body), handlerName)
}

fun Dispatcher.dice(handlerName: String? = null, body: HandleDice) {
    addHandler(DiceHandler(body), handlerName)
}
