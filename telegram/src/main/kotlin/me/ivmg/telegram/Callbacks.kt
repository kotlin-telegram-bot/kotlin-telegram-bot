package me.ivmg.telegram

import me.ivmg.telegram.entities.Animation
import me.ivmg.telegram.entities.Audio
import me.ivmg.telegram.entities.Contact
import me.ivmg.telegram.entities.Document
import me.ivmg.telegram.entities.Game
import me.ivmg.telegram.entities.InlineQuery
import me.ivmg.telegram.entities.Location
import me.ivmg.telegram.entities.PhotoSize
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.Video
import me.ivmg.telegram.entities.VideoNote
import me.ivmg.telegram.entities.Voice
import me.ivmg.telegram.entities.stickers.Sticker
import me.ivmg.telegram.errors.TelegramError

typealias HandleUpdate = (Bot, Update) -> Unit

typealias HandleError = (Bot, TelegramError) -> Unit

typealias CommandHandleUpdate = (Bot, Update, List<String>) -> Unit

typealias ContactHandleUpdate = (Bot, Update, Contact) -> Unit

typealias LocationHandleUpdate = (Bot, Update, Location) -> Unit

typealias HandleInlineQuery = (Bot, InlineQuery) -> Unit

typealias HandleAudioUpdate = (Bot, Update, Audio) -> Unit
typealias HandleDocumentUpdate = (Bot, Update, Document) -> Unit
typealias HandleAnimationUpdate = (Bot, Update, Animation) -> Unit
typealias HandleGameUpdate = (Bot, Update, Game) -> Unit
typealias HandlePhotosUpdate = (Bot, Update, List<PhotoSize>) -> Unit
typealias HandleStickerUpdate = (Bot, Update, Sticker) -> Unit
typealias HandleVideoUpdate = (Bot, Update, Video) -> Unit
typealias HandleVoiceUpdate = (Bot, Update, Voice) -> Unit
typealias HandleVideoNoteUpdate = (Bot, Update, VideoNote) -> Unit
