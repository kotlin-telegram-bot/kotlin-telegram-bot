package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.HandleAnimationUpdate
import com.github.kotlintelegrambot.HandleAudioUpdate
import com.github.kotlintelegrambot.HandleCallbackQuery
import com.github.kotlintelegrambot.HandleCommand
import com.github.kotlintelegrambot.HandleContact
import com.github.kotlintelegrambot.HandleDice
import com.github.kotlintelegrambot.HandleDocumentUpdate
import com.github.kotlintelegrambot.HandleError
import com.github.kotlintelegrambot.HandleGameUpdate
import com.github.kotlintelegrambot.HandleInlineQuery
import com.github.kotlintelegrambot.HandleLocation
import com.github.kotlintelegrambot.HandleMessage
import com.github.kotlintelegrambot.HandleNewChatMembers
import com.github.kotlintelegrambot.HandlePhotosUpdate
import com.github.kotlintelegrambot.HandlePollAnswer
import com.github.kotlintelegrambot.HandleStickerUpdate
import com.github.kotlintelegrambot.HandleText
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.HandleVideoNoteUpdate
import com.github.kotlintelegrambot.HandleVideoUpdate
import com.github.kotlintelegrambot.HandleVoiceUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ChannelHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CheckoutHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ContactHandler
import com.github.kotlintelegrambot.dispatcher.handlers.DiceHandler
import com.github.kotlintelegrambot.dispatcher.handlers.InlineQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.LocationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.dispatcher.handlers.NewChatMembersHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PollAnswerHandler
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

fun Dispatcher.message(handleMessage: HandleMessage) {
    addHandler(MessageHandler(All, handleMessage))
}

fun Dispatcher.message(filter: Filter, handleMessage: HandleMessage) {
    addHandler(MessageHandler(filter, handleMessage))
}

fun Dispatcher.command(command: String, handleCommand: HandleCommand) {
    addHandler(CommandHandler(command, handleCommand))
}

fun Dispatcher.text(text: String? = null, handleText: HandleText) {
    addHandler(TextHandler(text, handleText))
}

fun Dispatcher.callbackQuery(data: String? = null, handleCallbackQuery: HandleCallbackQuery) {
    addHandler(CallbackQueryHandler(callbackData = data, handleCallbackQuery = handleCallbackQuery))
}

fun Dispatcher.callbackQuery(
    callbackData: String? = null,
    callbackAnswerText: String? = null,
    callbackAnswerShowAlert: Boolean? = null,
    callbackAnswerUrl: String? = null,
    callbackAnswerCacheTime: Int? = null,
    handleCallbackQuery: HandleCallbackQuery
) {
    addHandler(
        CallbackQueryHandler(
            callbackData = callbackData,
            callbackAnswerText = callbackAnswerText,
            callbackAnswerShowAlert = callbackAnswerShowAlert,
            callbackAnswerUrl = callbackAnswerUrl,
            callbackAnswerCacheTime = callbackAnswerCacheTime,
            handleCallbackQuery = handleCallbackQuery
        )
    )
}

fun Dispatcher.contact(handleContact: HandleContact) {
    addHandler(ContactHandler(handleContact))
}

fun Dispatcher.location(handleLocation: HandleLocation) {
    addHandler(LocationHandler(handleLocation))
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(body)
}

fun Dispatcher.preCheckoutQuery(body: HandleUpdate) {
    addHandler(CheckoutHandler(body))
}

fun Dispatcher.channel(body: HandleUpdate) {
    addHandler(ChannelHandler(body))
}

fun Dispatcher.inlineQuery(body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body))
}

fun Dispatcher.audio(body: HandleAudioUpdate) {
    addHandler(AudioHandler(body))
}

fun Dispatcher.document(body: HandleDocumentUpdate) {
    addHandler(DocumentHandler(body))
}

fun Dispatcher.animation(body: HandleAnimationUpdate) {
    addHandler(AnimationHandler(body))
}

fun Dispatcher.game(body: HandleGameUpdate) {
    addHandler(GameHandler(body))
}

fun Dispatcher.photos(body: HandlePhotosUpdate) {
    addHandler(PhotosHandler(body))
}

fun Dispatcher.sticker(body: HandleStickerUpdate) {
    addHandler(StickerHandler(body))
}

fun Dispatcher.video(body: HandleVideoUpdate) {
    addHandler(VideoHandler(body))
}

fun Dispatcher.voice(body: HandleVoiceUpdate) {
    addHandler(VoiceHandler(body))
}

fun Dispatcher.videoNote(body: HandleVideoNoteUpdate) {
    addHandler(VideoNoteHandler(body))
}

fun Dispatcher.newChatMembers(body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body))
}

fun Dispatcher.pollAnswer(body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body))
}

fun Dispatcher.dice(body: HandleDice) {
    addHandler(DiceHandler(body))
}
