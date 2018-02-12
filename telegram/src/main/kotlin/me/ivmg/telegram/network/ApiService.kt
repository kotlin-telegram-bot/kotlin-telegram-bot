package me.ivmg.telegram.network

import me.ivmg.telegram.entities.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import java.io.File as SystemFile

interface ApiService {

    /**
     * Getting updates
     */
    @GET("getUpdates")
    fun getUpdates(
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int?,
        @Query("timeout") timeout: Int? = 10
    ): Call<Response<List<Update>>>

    // TODO webhooks

    /**
     * Available methods
     */

    @GET("getMe")
    fun getMe(): Call<Response<User>>

    @POST("sendMessage")
    @FormUrlEncoded
    fun sendMessage(
        @Field("chat_id") chatId: Long,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: String?,
        @Field("disable_web_page_preview") disableWebPagePreview: Boolean?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Int?,
        @Field("reply_markup") replyMarkup: ReplyMarkup?
    ): Call<Response<Message>>

    @POST("forwardMessage")
    @FormUrlEncoded
    fun forwardMessage(
        @Field("chat_id") chatId: Long,
        @Field("from_chat_id") fromChatId: Long,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("message_id") messageId: Long
    ): Call<Response<Message>>

    @POST("sendPhoto")
    @Multipart
    fun sendPhoto(
        @Part("chat_id") chatId: RequestBody,
        @Part("photo") photo: RequestBody,
        @Part("caption") caption: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendPhoto")
    @FormUrlEncoded
    fun sendPhoto(
        @Field("chat_id") chatId: Long,
        @Field("photo") fileId: String,
        @Field("caption") caption: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendAudio")
    @Multipart
    fun sendAudio(
        @Part("chat_id") chatId: RequestBody,
        @Part("audio") audio: RequestBody,
        @Part("duration") duration: RequestBody?,
        @Part("performer") performer: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendAudio")
    @FormUrlEncoded
    fun sendAudio(
        @Field("chat_id") chatId: Long,
        @Field("audio") fileId: String,
        @Field("duration") duration: Int?,
        @Field("performer") performer: String?,
        @Field("title") title: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Part("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendDocument")
    @Multipart
    fun sendDocument(
        @Part("chat_id") chatId: RequestBody,
        @Part("document") document: RequestBody,
        @Part("caption") caption: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendDocument")
    @FormUrlEncoded
    fun sendDocument(
        @Field("chat_id") chatId: Long,
        @Field("document") fileId: String,
        @Field("caption") caption: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendVideo")
    @Multipart
    fun sendVideo(
        @Part("chat_id") chatId: RequestBody,
        @Part("video") video: RequestBody,
        @Part("duration") duration: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendVideo")
    @FormUrlEncoded
    fun sendVideo(
        @Field("chat_id") chatId: Long,
        @Field("video") fileId: String,
        @Field("duration") duration: Int?,
        @Field("width") width: Int?,
        @Field("height") height: Int?,
        @Field("caption") caption: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendVoice")
    @Multipart
    fun sendVoice(
        @Part("chat_id") chatId: RequestBody,
        @Part("voice") voice: RequestBody,
        @Part("duration") duration: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendVoice")
    @FormUrlEncoded
    fun sendVoice(
        @Field("chat_id") chatId: Long,
        @Field("voice") fileId: String,
        @Field("duration") duration: Int?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendVideoNote")
    @Multipart
    fun sendVideoNote(
        @Part("chat_id") chatId: RequestBody,
        @Part("video_note") videoNote: RequestBody,
        @Part("duration") duration: RequestBody?,
        @Part("length") length: RequestBody?,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendVideoNote")
    @FormUrlEncoded
    fun sendVideoNote(
        @Field("chat_id") chatId: Long,
        @Field("video_note") fileId: String,
        @Field("duration") duration: Int?,
        @Field("length") length: Int?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendMediaGroup")
    @FormUrlEncoded
    fun sendMediaGroup(
        @Field("chat_id") chatId: Long,
        @Field("media") media: List<InputMedia>,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?
    ): Call<Response<Message>>

    @POST("sendLocation")
    @FormUrlEncoded
    fun sendLocation(
        @Field("chat_id") chatId: Long,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("live_period") livePeriod: Int?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("editMessageLiveLocation")
    @FormUrlEncoded
    fun editMessageLiveLocation(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("stopMessageLiveLocation")
    @FormUrlEncoded
    fun stopMessageLiveLocation(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendVenue")
    @FormUrlEncoded
    fun sendVenue(
        @Field("chat_id") chatId: Long,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("title") title: String,
        @Field("address") address: String,
        @Field("foursquare_id") foursquareId: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendContact")
    @FormUrlEncoded
    fun sendContact(
        @Field("chat_id") chatId: Long,
        @Field("phone_number") phoneNumber: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String?,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendChatAction")
    @FormUrlEncoded
    fun sendChatAction(
        @Field("chat_id") chatId: Long,
        @Field("action") action: ChatAction
    ): Call<Response<Boolean>>

    @GET("getUserProfilePhotos")
    fun getUserProfilePhotos(
        @Query("user_id") userId: Long,
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int? = null
    ): Call<Response<UserProfilePhotos>>

    @GET("getFile")
    fun getFile(
        @Query("file_id") fileId: String
    ): Call<Response<File>>

    @POST("kickChatMember")
    @FormUrlEncoded
    fun kickChatMember(
        @Field("chat_id") chatId: Long,
        @Field("user_id") userId: Long,
        @Field("until_date") untilDate: Date
    ): Call<Response<Boolean>>

    @POST("unbanChatMember")
    @FormUrlEncoded
    fun unbanChatMember(
        @Field("chat_id") chatId: Long,
        @Field("user_id") userId: Long
    ): Call<Response<Boolean>>

    @POST("restrictChatMember")
    @FormUrlEncoded
    fun restrictChatMember(
        @Field("chat_id") chatId: Long,
        @Field("user_id") userId: Long,
        @Field("until_date") untilDate: Date?,
        @Field("can_send_messages") canSendMessages: Boolean?,
        @Field("can_send_media_messages") canSendMediaMessages: Boolean?,
        @Field("can_send_other_messages") canSendOtherMessages: Boolean?,
        @Field("can_add_web_page_previews") canAddWebPagePreviews: Boolean?
    ): Call<Response<Boolean>>

    @POST("promoteChatMember")
    @FormUrlEncoded
    fun promoteChatMember(
        @Field("chat_id") chatId: Long,
        @Field("user_id") userId: Long,
        @Field("can_change_info") canChangeInfo: Boolean?,
        @Field("can_post_messages") canPostMessages: Boolean?,
        @Field("can_edit_messages") canEditMessages: Boolean?,
        @Field("can_delete_messages") canDeleteMessages: Boolean?,
        @Field("can_invite_users") canInviteUsers: Boolean?,
        @Field("can_restrict_members") canRestrictMembers: Boolean?,
        @Field("can_pin_messages") canPinMessages: Boolean?,
        @Field("can_promote_members") canPromoteMembers: Boolean?
    ): Call<Response<Boolean>>

    @POST("exportChatInviteLink")
    @FormUrlEncoded
    fun exportChatInviteLink(
        @Field("chat_id") chatId: Long
    ): Call<Response<String>>

    @POST("setChatPhoto")
    @FormUrlEncoded
    fun setChatPhoto(
        @Field("chat_id") chatId: Long,
        @Field("photo") photo: InputFile
    ): Call<Response<Boolean>>

    @POST("deleteChatPhoto")
    @FormUrlEncoded
    fun deleteChatPhoto(
        @Field("chat_id") chatId: Long
    ): Call<Response<Boolean>>

    @POST("setChatTitle")
    @FormUrlEncoded
    fun setChatTitle(
        @Field("chat_id") chatId: Long,
        @Field("title") title: String
    ): Call<Response<Boolean>>

    @POST("setChatDescription")
    @FormUrlEncoded
    fun setChatDescription(
        @Field("chat_id") chatId: Long,
        @Field("description") description: String
    ): Call<Response<Boolean>>

    @POST("pinChatMessage")
    @FormUrlEncoded
    fun pinChatMessage(
        @Field("chat_id") chatId: Long,
        @Field("message_id") messageId: Long,
        @Field("disable_notification") disableNotification: Boolean?
    ): Call<Response<Boolean>>

    @POST("unpinChatMessage")
    @FormUrlEncoded
    fun unpinChatMessage(
        @Field("chat_id") chatId: Long
    ): Call<Response<Boolean>>

    @POST("leaveChat")
    @FormUrlEncoded
    fun leaveChat(
        @Field("chat_id") chatId: Long
    ): Call<Response<Boolean>>

    @GET("getChat")
    fun getChat(
        @Query("chat_id") chatId: Long
    ): Call<Response<Chat>>

    @GET("getChatAdministrators")
    fun getChatAdministrators(
        @Query("chat_id") chatId: Long
    ): Call<Response<List<ChatMember>>>

    @GET("getChatMembersCount")
    fun getChatMembersCount(
        @Query("chat_id") chatId: Long
    ): Call<Response<Int>>

    @GET("getChatMember")
    fun getChatMember(
        @Query("chat_id") chatId: Long,
        @Query("user_id") userId: Long
    ): Call<Response<ChatMember>>

    @POST("setChatStickerSet")
    @FormUrlEncoded
    fun setChatStickerSet(
        @Field("chat_id") chatId: Long,
        @Field("sticker_set_name") stickerSetName: String
    ): Call<Response<Boolean>>

    @POST("deleteChatStickerSet")
    @FormUrlEncoded
    fun deleteChatStickerSet(
        @Field("chat_id") chatId: Long
    ): Call<Response<Boolean>>

    @POST("answerCallbackQuery")
    @FormUrlEncoded
    fun answerCallbackQuery(
        @Field("callback_query_id") callbackQueryId: String,
        @Field("text") text: String?,
        @Field("show_alert") showAlert: Boolean?,
        @Field("url") url: String?,
        @Field("cache_time") cacheTime: Int?
    ): Call<Response<Boolean>>

    /**
     * Updating messages
     */

    @POST("editMessageText")
    @FormUrlEncoded
    fun editMessageText(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: String?,
        @Field("disable_web_page_preview") disableWebPagePreview: Boolean?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("editMessageCaption")
    @FormUrlEncoded
    fun editMessageCaption(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("caption") caption: String,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("editMessageReplyMarkup")
    @FormUrlEncoded
    fun editMessageReplyMarkup(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("deleteMessage")
    @FormUrlEncoded
    fun deleteMessage(
        @Field("chat_id") chatId: Long?,
        @Field("message_id") messageId: Long?
    ): Call<Response<Message>>

    /***
     * Stickers
     */
    @POST("sendSticker")
    @Multipart
    fun sendSticker(
        @Part("chat_id") chatId: RequestBody,
        @Part("sticker") sticker: RequestBody,
        @Part("disable_notification") disableNotification: RequestBody?,
        @Part("reply_to_message_id") replyToMessageId: RequestBody?,
        @Part("reply_markup") replyMarkup: RequestBody? = null
    ): Call<Response<Message>>

    @POST("sendSticker")
    @FormUrlEncoded
    fun sendSticker(
        @Field("chat_id") chatId: Long,
        @Field("sticker") fileId: String,
        @Field("disable_notification") disableNotification: Boolean?,
        @Field("reply_to_message_id") replyToMessageId: Long?,
        @Field("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>
}