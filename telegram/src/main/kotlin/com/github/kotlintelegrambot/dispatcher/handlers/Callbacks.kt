package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.stickers.Sticker

typealias HandleUpdate = (Bot, Update) -> Unit

typealias HandleError = ErrorHandlerEnvironment.() -> Unit

typealias HandleMessage = MessageHandlerEnvironment.() -> Unit

typealias HandleCommand = CommandHandlerEnvironment.() -> Unit

typealias HandleText = TextHandlerEnvironment.() -> Unit

typealias HandleCallbackQuery = CallbackQueryHandlerEnvironment.() -> Unit

typealias HandleContact = ContactHandlerEnvironment.() -> Unit

typealias HandleLocation = LocationHandlerEnvironment.() -> Unit

typealias HandleInlineQuery = InlineQueryHandlerEnvironment.() -> Unit

typealias HandleNewChatMembers = NewChatMembersHandlerEnvironment.() -> Unit

typealias HandlePollAnswer = PollAnswerHandlerEnvironment.() -> Unit

typealias HandleDice = DiceHandlerEnvironment.() -> Unit

typealias HandleChannelPost = ChannelHandlerEnvironment.() -> Unit

typealias HandlePreCheckoutQuery = PreCheckoutQueryHandlerEnvironment.() -> Unit

typealias HandleAudio = MediaHandlerEnvironment<Audio>.() -> Unit
typealias HandleDocument = MediaHandlerEnvironment<Document>.() -> Unit
typealias HandleAnimation = MediaHandlerEnvironment<Animation>.() -> Unit
typealias HandleGame = MediaHandlerEnvironment<Game>.() -> Unit
typealias HandlePhotos = MediaHandlerEnvironment<List<PhotoSize>>.() -> Unit
typealias HandleSticker = MediaHandlerEnvironment<Sticker>.() -> Unit
typealias HandleVideo = MediaHandlerEnvironment<Video>.() -> Unit
typealias HandleVoice = MediaHandlerEnvironment<Voice>.() -> Unit
typealias HandleVideoNote = MediaHandlerEnvironment<VideoNote>.() -> Unit
