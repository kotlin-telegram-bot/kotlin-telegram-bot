package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.UserProfilePhotos
import com.github.kotlintelegrambot.entities.WebhookInfo
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.dice.DiceFields
import com.github.kotlintelegrambot.entities.files.File
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.payments.LabeledPrice
import com.github.kotlintelegrambot.entities.payments.ShippingOption
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollFields
import com.github.kotlintelegrambot.entities.polls.PollType
import com.github.kotlintelegrambot.entities.stickers.MaskPosition
import com.github.kotlintelegrambot.entities.stickers.StickerSet
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

internal interface ApiService {

    @GET("getUpdates")
    suspend fun getUpdates(
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int?,
        @Query("timeout") timeout: Int?,
        @Query("allowed_updates") allowedUpdates: String?,
    ): CallResponse<Response<List<Update>>>

    @FormUrlEncoded
    @POST("setWebhook")
    suspend fun setWebhook(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setWebhook")
    suspend fun setWebhookWithCertificateAsFileId(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.CERTIFICATE) certificateFileId: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setWebhook")
    suspend fun setWebhookWithCertificateAsFileUrl(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.CERTIFICATE) certificateUrl: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null
    ): CallResponse<Response<Boolean>>

    @Multipart
    @POST("setWebhook")
    suspend fun setWebhookWithCertificateAsFile(
        @Part url: MultipartBody.Part,
        @Part certificate: MultipartBody.Part,
        @Part ipAddress: MultipartBody.Part? = null,
        @Part maxConnections: MultipartBody.Part? = null,
        @Part allowedUpdates: MultipartBody.Part? = null
    ): CallResponse<Response<Boolean>>

    @GET("deleteWebhook")
    suspend fun deleteWebhook(): CallResponse<Response<Boolean>>

    @GET("getWebhookInfo")
    suspend fun getWebhookInfo(): CallResponse<Response<WebhookInfo>>

    /**
     * Available methods
     */

    @GET("getMe")
    suspend fun getMe(): CallResponse<Response<User>>

    @FormUrlEncoded
    @POST("sendMessage")
    suspend fun sendMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("disable_web_page_preview") disableWebPagePreview: Boolean?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("forwardMessage")
    suspend fun forwardMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field("message_id") messageId: Long
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("copyMessage")
    suspend fun copyMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("caption_entities") captionEntities: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup?
    ): CallResponse<Response<MessageId>>

    @Multipart
    @POST("sendPhoto")
    suspend fun sendPhoto(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part photo: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendPhoto")
    suspend fun sendPhoto(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("photo") photo: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @Multipart
    @POST("sendAudio")
    suspend fun sendAudio(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part audio: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("performer") performer: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendAudio")
    suspend fun sendAudio(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("audio") audio: String,
        @Field("duration") duration: Int?,
        @Field("performer") performer: String?,
        @Field("title") title: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @POST("sendDocument")
    @Multipart
    suspend fun sendDocument(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part document: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_CONTENT_TYPE_DETECTION) disableContentTypeDetection: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendDocument")
    suspend fun sendDocument(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("document") document: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_CONTENT_TYPE_DETECTION) disableContentTypeDetection: Boolean?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @Multipart
    @POST("sendVideo")
    suspend fun sendVideo(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part video: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendVideo")
    suspend fun sendVideo(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("video") video: String,
        @Field("duration") duration: Int?,
        @Field("width") width: Int?,
        @Field("height") height: Int?,
        @Field("caption") caption: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendGame")
    suspend fun sendGame(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.SendGame.GAME_SHORT_NAME) gameShortName: String,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @Multipart
    @POST("sendAnimation")
    suspend fun sendAnimation(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part animation: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendAnimation")
    suspend fun sendAnimation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("animation") animation: String,
        @Field("duration") duration: Int?,
        @Field("width") width: Int?,
        @Field("height") height: Int?,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @Multipart
    @POST("sendVoice")
    suspend fun sendVoice(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part voice: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part("caption_entities") captionEntities: RequestBody?,
        @Part("duration") duration: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendVoice")
    suspend fun sendVoice(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("voice") voice: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("caption_entities") captionEntities: String?,
        @Field("duration") duration: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @POST("sendVideoNote")
    @Multipart
    suspend fun sendVideoNote(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part videoNote: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("length") length: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendVideoNote")
    suspend fun sendVideoNote(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("video_note") fileId: String,
        @Field("duration") duration: Int?,
        @Field("length") length: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @Multipart
    @POST("sendMediaGroup")
    suspend fun sendMediaGroup(@Part body: List<MultipartBody.Part>): CallResponse<Response<List<Message>>>

    @FormUrlEncoded
    @POST("sendLocation")
    suspend fun sendLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("live_period") livePeriod: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageLiveLocation")
    suspend fun editMessageLiveLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("stopMessageLiveLocation")
    suspend fun stopMessageLiveLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendVenue")
    suspend fun sendVenue(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("title") title: String,
        @Field("address") address: String,
        @Field("foursquare_id") foursquareId: String?,
        @Field("foursquare_type") foursquareType: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @POST("sendContact")
    @FormUrlEncoded
    suspend fun sendContact(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("phone_number") phoneNumber: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendPoll")
    suspend fun sendPoll(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(PollFields.QUESTION) question: String,
        @Field(PollFields.OPTIONS) options: String,
        @Field(PollFields.IS_ANONYMOUS) isAnonymous: Boolean? = null,
        @Field(PollFields.TYPE) type: PollType? = null,
        @Field(PollFields.ALLOWS_MULTIPLE_ANSWERS) allowsMultipleAnswers: Boolean? = null,
        @Field(PollFields.CORRECT_OPTION_ID) correctOptionId: Int? = null,
        @Field(PollFields.EXPLANATION) explanation: String? = null,
        @Field(PollFields.EXPLANATION_PARSE_MODE) explanationParseMode: ParseMode? = null,
        @Field(PollFields.OPEN_PERIOD) openPeriod: Int? = null,
        @Field(PollFields.CLOSE_DATE) closeDate: Long? = null,
        @Field(PollFields.IS_CLOSED) isClosed: Boolean? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendChatAction")
    suspend fun sendChatAction(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("action") action: ChatAction
    ): CallResponse<Response<Boolean>>

    @GET("getUserProfilePhotos")
    suspend fun getUserProfilePhotos(
        @Query(ApiConstants.USER_ID) userId: Long,
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int? = null
    ): CallResponse<Response<UserProfilePhotos>>

    @GET("getFile")
    suspend fun getFile(
        @Query("file_id") fileId: String
    ): CallResponse<Response<File>>

    @GET
    suspend fun downloadFile(
        @Url customUrl: String
    ): CallResponse<ResponseBody>

    @FormUrlEncoded
    @POST("banChatMember")
    suspend fun banChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("until_date") untilDate: Long?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("unbanChatMember")
    suspend fun unbanChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("only_if_banned") onlyIfBanned: Boolean?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("restrictChatMember")
    suspend fun restrictChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("permissions") permissions: String,
        @Field("until_date") untilDate: Long?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("promoteChatMember")
    suspend fun promoteChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("is_anonymous") isAnonymous: Boolean?,
        @Field("can_change_info") canChangeInfo: Boolean?,
        @Field("can_post_messages") canPostMessages: Boolean?,
        @Field("can_edit_messages") canEditMessages: Boolean?,
        @Field("can_delete_messages") canDeleteMessages: Boolean?,
        @Field("can_invite_users") canInviteUsers: Boolean?,
        @Field("can_restrict_members") canRestrictMembers: Boolean?,
        @Field("can_pin_messages") canPinMessages: Boolean?,
        @Field("can_promote_members") canPromoteMembers: Boolean?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatPermissions")
    suspend fun setChatPermissions(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("permissions") permissions: String
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("exportChatInviteLink")
    suspend fun exportChatInviteLink(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<String>>

    @Multipart
    @POST("setChatPhoto")
    suspend fun setChatPhoto(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part("photo") photo: MultipartBody.Part
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteChatPhoto")
    suspend fun deleteChatPhoto(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatTitle")
    suspend fun setChatTitle(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("title") title: String
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatDescription")
    suspend fun setChatDescription(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("description") description: String
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("pinChatMessage")
    suspend fun pinChatMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("unpinChatMessage")
    suspend fun unpinChatMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("unpinAllChatMessages")
    suspend fun unpinAllChatMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("leaveChat")
    suspend fun leaveChat(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Boolean>>

    @GET("getChat")
    suspend fun getChat(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Chat>>

    @GET("getChatAdministrators")
    suspend fun getChatAdministrators(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<List<ChatMember>>>

    @GET("getChatMemberCount")
    suspend fun getChatMemberCount(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Int>>

    @GET("getChatMember")
    suspend fun getChatMember(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
        @Query(ApiConstants.USER_ID) userId: Long
    ): CallResponse<Response<ChatMember>>

    @FormUrlEncoded
    @POST("setChatStickerSet")
    suspend fun setChatStickerSet(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("sticker_set_name") stickerSetName: String
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteChatStickerSet")
    suspend fun deleteChatStickerSet(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerCallbackQuery")
    suspend fun answerCallbackQuery(
        @Field("callback_query_id") callbackQueryId: String,
        @Field("text") text: String?,
        @Field("show_alert") showAlert: Boolean?,
        @Field("url") url: String?,
        @Field("cache_time") cacheTime: Int?
    ): CallResponse<Response<Boolean>>

    @GET("logOut")
    suspend fun logOut(): CallResponse<Response<Boolean>>

    /**
     * Updating messages
     */

    @FormUrlEncoded
    @POST("editMessageText")
    suspend fun editMessageText(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("disable_web_page_preview") disableWebPagePreview: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageCaption")
    suspend fun editMessageCaption(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("caption") caption: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageMedia")
    suspend fun editMessageMedia(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("media") media: InputMedia,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageReplyMarkup")
    suspend fun editMessageReplyMarkup(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("stopPoll")
    suspend fun stopPoll(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Poll>>

    @FormUrlEncoded
    @POST("deleteMessage")
    suspend fun deleteMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long
    ): CallResponse<Response<Boolean>>

    /***
     * Stickers
     */

    @Multipart
    @POST("sendSticker")
    suspend fun sendSticker(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part("sticker") sticker: MultipartBody.Part,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: RequestBody?,
        @Part(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("sendSticker")
    suspend fun sendSticker(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("sticker") fileId: String,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @GET("getStickerSet")
    suspend fun getStickerSet(
        @Query("name") name: String
    ): CallResponse<Response<StickerSet>>

    @Multipart
    @POST("uploadStickerFile")
    suspend fun uploadStickerFile(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part
    ): CallResponse<Response<File>>

    @Multipart
    @POST("createNewStickerSet")
    suspend fun createNewStickerSet(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("title") title: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part,
        @Part("emojis") emojis: RequestBody,
        @Part("contains_masks") containsMasks: RequestBody?,
        @Part("mask_position") maskPosition: RequestBody?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("createNewStickerSet")
    suspend fun createNewStickerSet(
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("name") name: String,
        @Field("title") title: String,
        @Field("png_sticker") fileId: String,
        @Field("emojis") emojis: String,
        @Field("contains_masks") containsMasks: Boolean?,
        @Field("mask_position") maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>>

    @Multipart
    @POST("addStickerToSet")
    suspend fun addStickerToSet(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part,
        @Part("emojis") emojis: RequestBody,
        @Part("mask_position") maskPosition: RequestBody?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("addStickerToSet")
    suspend fun addStickerToSet(
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("name") name: String,
        @Field("png_sticker") fileId: String,
        @Field("emojis") emojis: String,
        @Field("mask_position") maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("setStickerPositionInSet")
    suspend fun setStickerPositionInSet(
        @Part("sticker") sticker: String,
        @Part("position") position: Int
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteStickerFromSet")
    suspend fun deleteStickerFromSet(
        @Part("sticker") sticker: String
    ): CallResponse<Response<Boolean>>

    /**
     * Payment
     */

    @FormUrlEncoded
    @POST("sendInvoice")
    suspend fun sendInvoice(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("payload") payload: String,
        @Field("provider_token") providerToken: String,
        @Field("start_parameter") startParameter: String,
        @Field("currency") currency: String,
        @Field("prices") prices: LabeledPriceList,
        @Field("provider_data") providerData: String?,
        @Field("photo_url") photoUrl: String?,
        @Field("photo_size") photoSize: Int?,
        @Field("photo_width") photoWidth: Int?,
        @Field("photo_height") photoHeight: Int?,
        @Field("need_name") needName: Boolean?,
        @Field("need_phone_number") needPhoneNumber: Boolean?,
        @Field("need_email") needEmail: Boolean?,
        @Field("need_shipping_address") needShippingAddress: Boolean?,
        @Field("send_phone_number_to_provider") sendPhoneNumberToProvider: Boolean?,
        @Field("send_email_to_provider") sendEmailToProvider: Boolean?,
        @Field("is_flexible") isFlexible: Boolean?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long?,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST("answerShippingQuery")
    suspend fun answerShippingQuery(
        @Field("shipping_query_id") shippingQueryId: String,
        @Field("ok") ok: Boolean,
        @Field("shipping_options") shippingOptions: List<ShippingOption>? = null,
        @Field("error_message") errorMessage: String? = null
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerPreCheckoutQuery")
    suspend fun answerPreCheckoutQuery(
        @Field("pre_checkout_query_id") preCheckoutQueryId: String,
        @Field("ok") ok: Boolean,
        @Field("error_message") errorMessage: String? = null
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerInlineQuery")
    suspend fun answerInlineQuery(
        @Field("inline_query_id") inlineQueryId: String,
        @Field("results") inlineQueryResults: String,
        @Field("cache_time") cacheTime: Int?,
        @Field("is_personal") isPersonal: Boolean,
        @Field("next_offset") nextOffset: String?,
        @Field("switch_pm_text") switchPmText: String?,
        @Field("switch_pm_parameter") switchPmParameter: String?
    ): CallResponse<Response<Boolean>>

    @GET("getMyCommands")
    suspend fun getMyCommands(): CallResponse<Response<List<BotCommand>>>

    @FormUrlEncoded
    @POST("setMyCommands")
    suspend fun setMyCommands(
        @Field("commands") commands: String
    ): CallResponse<Response<Boolean>>

    @FormUrlEncoded
    @POST(DiceFields.SEND_DICE_OP_NAME)
    suspend fun sendDice(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(DiceFields.EMOJI) emoji: DiceEmoji? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.REPLY_TO_MESSAGE_ID) replyToMessageId: Long? = null,
        @Field(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY) allowSendingWithoutReply: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<Message>>

    @FormUrlEncoded
    @POST(ApiConstants.SetChatAdministratorCustomTitle.OP_NAME)
    suspend fun setChatAdministratorCustomTitle(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field(ApiConstants.SetChatAdministratorCustomTitle.CUSTOM_TITLE) customTitle: String
    ): CallResponse<Response<Boolean>>
}

internal class LabeledPriceList(private val labeledPrice: List<LabeledPrice>) {

    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String {
        return GSON.toJson(labeledPrice)
    }
}
