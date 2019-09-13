package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.entities.payments.SuccessfulPayment
import me.ivmg.telegram.entities.stickers.Sticker

data class Message(
    @Name("message_id") val messageId: Long,
    val from: User? = null,
    val date: Int,
    val chat: Chat,
    @Name("forward_from") val forwardFrom: User? = null,
    @Name("forward_from_chat") val forwardFromChat: Chat? = null,
    @Name("forward_from_message_id") val forwardFromMessageId: Int? = null,
    @Name("forward_signature") val forwardSignature: String? = null,
    @Name("forward_sender_name") val forwardSenderName: String? = null,
    @Name("forward_date") val forwardDate: Int? = null,
    @Name("reply_to_message") val replyToMessage: Message? = null,
    @Name("edit_date") val editDate: Int? = null,
    val text: String? = null,
    val entities: List<MessageEntity>? = null,
    @Name("caption_entities") val captionEntities: List<MessageEntity>? = null,
    val audio: Audio? = null,
    val document: Document? = null,
    val animation: Animation? = null,
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
    @Name("new_chat_member") val newChatMember: User? = null,
    @Name("left_chat_member") val leftChatMember: User? = null,
    @Name("new_chat_title") val newChatTitle: String? = null,
    @Name("new_chat_photo") val newChatPhoto: List<PhotoSize>? = null,
    @Name("delete_chat_photo") val deleteChatPhoto: Boolean? = null,
    @Name("group_chat_created") val groupChatCreated: Boolean?,
    @Name("supergroup_chat_created") val supergroupChatCreated: Boolean? = null,
    @Name("channel_chat_created") val channelChatCreated: Boolean? = null,
    @Name("migrate_to_chat_id") val migrateToChatId: Long? = null,
    @Name("migrate_from_chat_id") val migrateFromChatId: Long? = null,
    val invoice: Invoice? = null,
    @Name("successful_payment") val successfulPayment: SuccessfulPayment? = null,
    @Name("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null
)
