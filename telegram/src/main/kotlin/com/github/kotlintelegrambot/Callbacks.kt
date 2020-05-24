package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.entities.Animation
import com.github.kotlintelegrambot.entities.Audio
import com.github.kotlintelegrambot.entities.Contact
import com.github.kotlintelegrambot.entities.Document
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.InlineQuery
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.PhotoSize
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.Video
import com.github.kotlintelegrambot.entities.VideoNote
import com.github.kotlintelegrambot.entities.Voice
import com.github.kotlintelegrambot.entities.polls.PollAnswer
import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.github.kotlintelegrambot.errors.TelegramError

typealias HandleUpdate = (Bot, Update) -> Unit

typealias HandleError = (Bot, TelegramError) -> Unit

typealias CommandHandleUpdate = (Bot, Update, List<String>) -> Unit

typealias ContactHandleUpdate = (Bot, Update, Contact) -> Unit

typealias LocationHandleUpdate = (Bot, Update, Location) -> Unit

typealias HandleInlineQuery = (Bot, InlineQuery) -> Unit

typealias HandleNewChatMembers = (Bot, Message, List<User>) -> Unit

typealias HandlePollAnswer = (Bot, PollAnswer) -> Unit

typealias HandleAudioUpdate = (Bot, Update, Audio) -> Unit
typealias HandleDocumentUpdate = (Bot, Update, Document) -> Unit
typealias HandleAnimationUpdate = (Bot, Update, Animation) -> Unit
typealias HandleGameUpdate = (Bot, Update, Game) -> Unit
typealias HandlePhotosUpdate = (Bot, Update, List<PhotoSize>) -> Unit
typealias HandleStickerUpdate = (Bot, Update, Sticker) -> Unit
typealias HandleVideoUpdate = (Bot, Update, Video) -> Unit
typealias HandleVoiceUpdate = (Bot, Update, Voice) -> Unit
typealias HandleVideoNoteUpdate = (Bot, Update, VideoNote) -> Unit
