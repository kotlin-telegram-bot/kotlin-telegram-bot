package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.payments.SuccessfulPayment
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.google.gson.annotations.SerializedName as Name

data class Message(
    @Name("message_id") val messageId: Long,
    val from: User? = null,
    @Name("sender_chat") val senderChat: Chat? = null,
    val date: Long,
    val chat: Chat,
    @Name("forward_from") val forwardFrom: User? = null,
    @Name("forward_from_chat") val forwardFromChat: Chat? = null,
    @Name("forward_from_message_id") val forwardFromMessageId: Int? = null,
    @Name("forward_signature") val forwardSignature: String? = null,
    @Name("forward_sender_name") val forwardSenderName: String? = null,
    @Name("forward_date") val forwardDate: Int? = null,
    @Name("reply_to_message") val replyToMessage: Message? = null,
    @Name("via_bot") val viaBot: User? = null,
    @Name("edit_date") val editDate: Int? = null,
    @Name("media_group_id") val mediaGroupId: String? = null,
    @Name("author_signature") val authorSignature: String? = null,
    val text: String? = null,
    val entities: List<MessageEntity>? = null,
    @Name("caption_entities") val captionEntities: List<MessageEntity>? = null,
    val audio: Audio? = null,
    val document: Document? = null,
    val animation: Animation? = null,
    @Name("dice") val dice: Dice? = null,
    val game: Game? = null,
    val photo: List<PhotoSize>? = null,
    val sticker: Sticker? = null,
    val video: Video? = null,
    val voice: Voice? = null,
    @Name("video_note") val videoNote: VideoNote? = null,
    val caption: String? = null,
    val contact: Contact? = null,
    val location: Location? = null,
    val venue: Venue? = null,
    @Name("new_chat_members") val newChatMembers: List<User>? = null,
    val poll: Poll? = null,
    @Name("left_chat_member") val leftChatMember: User? = null,
    @Name("new_chat_title") val newChatTitle: String? = null,
    @Name("new_chat_photo") val newChatPhoto: List<PhotoSize>? = null,
    @Name("delete_chat_photo") val deleteChatPhoto: Boolean? = null,
    @Name("group_chat_created") val groupChatCreated: Boolean? = null,
    @Name("supergroup_chat_created") val supergroupChatCreated: Boolean? = null,
    @Name("channel_chat_created") val channelChatCreated: Boolean? = null,
    @Name("migrate_to_chat_id") val migrateToChatId: Long? = null,
    @Name("migrate_from_chat_id") val migrateFromChatId: Long? = null,
    val invoice: Invoice? = null,
    @Name("successful_payment") val successfulPayment: SuccessfulPayment? = null,
    @Name("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
    @Name("proximity_alert_triggered") val proximityAlertTriggered: ProximityAlertTriggered? = null
)
