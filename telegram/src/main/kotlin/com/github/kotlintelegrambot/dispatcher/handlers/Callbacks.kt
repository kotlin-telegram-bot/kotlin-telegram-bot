package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.stickers.Sticker

typealias HandleError = ErrorHandlerEnvironment.() -> Unit
typealias HandleMessage = suspend MessageHandlerEnvironment.() -> Unit
typealias HandleCommand = suspend CommandHandlerEnvironment.() -> Unit
typealias HandleText = suspend TextHandlerEnvironment.() -> Unit
typealias HandleCallbackQuery = suspend CallbackQueryHandlerEnvironment.() -> Unit
typealias HandleContact = suspend ContactHandlerEnvironment.() -> Unit
typealias HandleLocation = suspend LocationHandlerEnvironment.() -> Unit
typealias HandleInlineQuery = suspend InlineQueryHandlerEnvironment.() -> Unit
typealias HandleNewChatMembers = suspend NewChatMembersHandlerEnvironment.() -> Unit
typealias HandlePollAnswer = suspend PollAnswerHandlerEnvironment.() -> Unit
typealias HandleDice = suspend DiceHandlerEnvironment.() -> Unit
typealias HandleChannelPost = suspend ChannelHandlerEnvironment.() -> Unit
typealias HandlePreCheckoutQuery = suspend PreCheckoutQueryHandlerEnvironment.() -> Unit
typealias HandleAudio = suspend MediaHandlerEnvironment<Audio>.() -> Unit
typealias HandleDocument = suspend MediaHandlerEnvironment<Document>.() -> Unit
typealias HandleAnimation = suspend MediaHandlerEnvironment<Animation>.() -> Unit
typealias HandleGame = suspend MediaHandlerEnvironment<Game>.() -> Unit
typealias HandlePhotos = suspend MediaHandlerEnvironment<List<PhotoSize>>.() -> Unit
typealias HandleSticker = suspend MediaHandlerEnvironment<Sticker>.() -> Unit
typealias HandleVideo = suspend MediaHandlerEnvironment<Video>.() -> Unit
typealias HandleVoice = suspend MediaHandlerEnvironment<Voice>.() -> Unit
typealias HandleVideoNote = suspend MediaHandlerEnvironment<VideoNote>.() -> Unit
