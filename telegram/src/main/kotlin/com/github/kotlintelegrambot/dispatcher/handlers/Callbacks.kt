package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.polls.PollAnswer
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

typealias HandlePollAnswer = (Bot, PollAnswer) -> Unit

typealias HandleDice = (Bot, Message, Dice) -> Unit

typealias HandleAudioUpdate = (Bot, Update, Audio) -> Unit
typealias HandleDocumentUpdate = (Bot, Update, Document) -> Unit
typealias HandleAnimationUpdate = (Bot, Update, Animation) -> Unit
typealias HandleGameUpdate = (Bot, Update, Game) -> Unit
typealias HandlePhotosUpdate = (Bot, Update, List<PhotoSize>) -> Unit
typealias HandleStickerUpdate = (Bot, Update, Sticker) -> Unit
typealias HandleVideoUpdate = (Bot, Update, Video) -> Unit
typealias HandleVoiceUpdate = (Bot, Update, Voice) -> Unit
typealias HandleVideoNoteUpdate = (Bot, Update, VideoNote) -> Unit
