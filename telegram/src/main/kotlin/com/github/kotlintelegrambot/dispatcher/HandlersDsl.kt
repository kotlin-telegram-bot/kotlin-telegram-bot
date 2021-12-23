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

public fun Dispatcher.message(handleMessage: HandleMessage) {
    addHandler(MessageHandler(All, handleMessage))
}

public fun Dispatcher.message(filter: Filter, handleMessage: HandleMessage) {
    addHandler(MessageHandler(filter, handleMessage))
}

public fun Dispatcher.command(command: String, handleCommand: HandleCommand) {
    addHandler(CommandHandler(command, handleCommand))
}

public fun Dispatcher.text(text: String? = null, handleText: HandleText) {
    addHandler(TextHandler(text, handleText))
}

public fun Dispatcher.callbackQuery(data: String? = null, handleCallbackQuery: HandleCallbackQuery) {
    addHandler(CallbackQueryHandler(callbackData = data, handleCallbackQuery = handleCallbackQuery))
}

public fun Dispatcher.callbackQuery(
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

public fun Dispatcher.contact(handleContact: HandleContact) {
    addHandler(ContactHandler(handleContact))
}

public fun Dispatcher.location(handleLocation: HandleLocation) {
    addHandler(LocationHandler(handleLocation))
}

public fun Dispatcher.telegramError(handleError: HandleError) {
    addErrorHandler(ErrorHandler(handleError))
}

public fun Dispatcher.preCheckoutQuery(body: HandlePreCheckoutQuery) {
    addHandler(PreCheckoutQueryHandler(body))
}

public fun Dispatcher.channel(body: HandleChannelPost) {
    addHandler(ChannelHandler(body))
}

public fun Dispatcher.inlineQuery(body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body))
}

public fun Dispatcher.audio(body: HandleAudio) {
    addHandler(AudioHandler(body))
}

public fun Dispatcher.document(body: HandleDocument) {
    addHandler(DocumentHandler(body))
}

public fun Dispatcher.animation(body: HandleAnimation) {
    addHandler(AnimationHandler(body))
}

public fun Dispatcher.game(body: HandleGame) {
    addHandler(GameHandler(body))
}

public fun Dispatcher.photos(body: HandlePhotos) {
    addHandler(PhotosHandler(body))
}

public fun Dispatcher.sticker(body: HandleSticker) {
    addHandler(StickerHandler(body))
}

public fun Dispatcher.video(body: HandleVideo) {
    addHandler(VideoHandler(body))
}

public fun Dispatcher.voice(body: HandleVoice) {
    addHandler(VoiceHandler(body))
}

public fun Dispatcher.videoNote(body: HandleVideoNote) {
    addHandler(VideoNoteHandler(body))
}

public fun Dispatcher.newChatMembers(body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body))
}

public fun Dispatcher.pollAnswer(body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body))
}

public fun Dispatcher.dice(body: HandleDice) {
    addHandler(DiceHandler(body))
}
