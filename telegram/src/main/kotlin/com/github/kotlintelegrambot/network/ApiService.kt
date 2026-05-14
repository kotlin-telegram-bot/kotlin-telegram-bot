package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.SentWebAppMessage
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
import retrofit2.Call
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
    fun getUpdates(
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int?,
        @Query("timeout") timeout: Int?,
        @Query("allowed_updates") allowedUpdates: String?,
    ): Call<Response<List<Update>>>

    @FormUrlEncoded
    @POST("setWebhook")
    fun setWebhook(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null,
        @Field(ApiConstants.SetWebhook.DROP_PENDING_UPDATES) dropPendingUpdates: Boolean? = null,
        @Field(ApiConstants.SetWebhook.SECRET_TOKEN) secretToken: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setWebhook")
    fun setWebhookWithCertificateAsFileId(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.CERTIFICATE) certificateFileId: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null,
        @Field(ApiConstants.SetWebhook.DROP_PENDING_UPDATES) dropPendingUpdates: Boolean? = null,
        @Field(ApiConstants.SetWebhook.SECRET_TOKEN) secretToken: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setWebhook")
    fun setWebhookWithCertificateAsFileUrl(
        @Field(ApiConstants.SetWebhook.URL) url: String,
        @Field(ApiConstants.SetWebhook.CERTIFICATE) certificateUrl: String,
        @Field(ApiConstants.SetWebhook.IP_ADDRESS) ipAddress: String? = null,
        @Field(ApiConstants.SetWebhook.MAX_CONNECTIONS) maxConnections: Int? = null,
        @Field(ApiConstants.SetWebhook.ALLOWED_UPDATES) allowedUpdates: List<String>? = null,
        @Field(ApiConstants.SetWebhook.DROP_PENDING_UPDATES) dropPendingUpdates: Boolean? = null,
        @Field(ApiConstants.SetWebhook.SECRET_TOKEN) secretToken: String? = null,
    ): Call<Response<Boolean>>

    @Multipart
    @POST("setWebhook")
    fun setWebhookWithCertificateAsFile(
        @Part url: MultipartBody.Part,
        @Part certificate: MultipartBody.Part,
        @Part ipAddress: MultipartBody.Part? = null,
        @Part maxConnections: MultipartBody.Part? = null,
        @Part allowedUpdates: MultipartBody.Part? = null,
        @Part dropPendingUpdates: MultipartBody.Part? = null,
        @Part secretToken: MultipartBody.Part? = null,
    ): Call<Response<Boolean>>

    @GET("deleteWebhook")
    fun deleteWebhook(
        @Query(ApiConstants.SetWebhook.DROP_PENDING_UPDATES) dropPendingUpdates: Boolean? = null,
    ): Call<Response<Boolean>>

    @GET("getWebhookInfo")
    fun getWebhookInfo(): Call<Response<WebhookInfo>>

    /**
     * Available methods
     */

    @GET("getMe")
    fun getMe(): Call<Response<User>>

    @FormUrlEncoded
    @POST("sendMessage")
    fun sendMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup?,
        @Field(ApiConstants.MESSAGE_THREAD_ID) messageThreadId: Long?,
        @Field("entities") entities: String? = null,
        @Field("link_preview_options") linkPreviewOptions: String? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("forwardMessage")
    fun forwardMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field("message_id") messageId: Long,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("copyMessage")
    fun copyMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("caption_entities") captionEntities: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup?,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<MessageId>>

    @Multipart
    @POST("sendPhoto")
    fun sendPhoto(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part photo: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part(ApiConstants.MESSAGE_THREAD_ID) messageThreadId: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendPhoto")
    fun sendPhoto(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("photo") photo: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @Multipart
    @POST("sendAudio")
    fun sendAudio(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part audio: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("performer") performer: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendAudio")
    fun sendAudio(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("audio") audio: String,
        @Field("duration") duration: Int?,
        @Field("performer") performer: String?,
        @Field("title") title: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @POST("sendDocument")
    @Multipart
    fun sendDocument(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part document: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_CONTENT_TYPE_DETECTION) disableContentTypeDetection: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendDocument")
    fun sendDocument(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("document") document: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_CONTENT_TYPE_DETECTION) disableContentTypeDetection: Boolean?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @Multipart
    @POST("sendVideo")
    fun sendVideo(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part video: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendVideo")
    fun sendVideo(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("video") video: String,
        @Field("duration") duration: Int?,
        @Field("width") width: Int?,
        @Field("height") height: Int?,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendGame")
    fun sendGame(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.SendGame.GAME_SHORT_NAME) gameShortName: String,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @Multipart
    @POST("sendAnimation")
    fun sendAnimation(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part animation: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendAnimation")
    fun sendAnimation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("animation") animation: String,
        @Field("duration") duration: Int?,
        @Field("width") width: Int?,
        @Field("height") height: Int?,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @Multipart
    @POST("sendVoice")
    fun sendVoice(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part voice: MultipartBody.Part,
        @Part("caption") caption: RequestBody?,
        @Part("parse_mode") parseMode: RequestBody?,
        @Part("caption_entities") captionEntities: RequestBody?,
        @Part("duration") duration: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
        @Part("show_caption_above_media") showCaptionAboveMedia: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendVoice")
    fun sendVoice(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("voice") voice: String,
        @Field("caption") caption: String?,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field("caption_entities") captionEntities: String?,
        @Field("duration") duration: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @POST("sendVideoNote")
    @Multipart
    fun sendVideoNote(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part videoNote: MultipartBody.Part,
        @Part("duration") duration: RequestBody?,
        @Part("length") length: RequestBody?,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendVideoNote")
    fun sendVideoNote(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("video_note") fileId: String,
        @Field("duration") duration: Int?,
        @Field("length") length: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @Multipart
    @POST("sendMediaGroup")
    fun sendMediaGroup(@Part body: List<MultipartBody.Part>): Call<Response<List<Message>>>

    @FormUrlEncoded
    @POST("sendLocation")
    fun sendLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("live_period") livePeriod: Int?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("proximity_alert_radius") proximityAlertRadius: Int? = null,
        @Field("horizontal_accuracy") horizontalAccuracy: Float? = null,
        @Field("heading") heading: Int? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageLiveLocation")
    fun editMessageLiveLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("proximity_alert_radius") proximityAlertRadius: Int? = null,
        @Field("horizontal_accuracy") horizontalAccuracy: Float? = null,
        @Field("heading") heading: Int? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("stopMessageLiveLocation")
    fun stopMessageLiveLocation(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendVenue")
    fun sendVenue(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("latitude") latitude: Float,
        @Field("longitude") longitude: Float,
        @Field("title") title: String,
        @Field("address") address: String,
        @Field("foursquare_id") foursquareId: String?,
        @Field("foursquare_type") foursquareType: String?,
        @Field("google_place_id") googlePlaceId: String?,
        @Field("google_place_type") googlePlaceType: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @POST("sendContact")
    @FormUrlEncoded
    fun sendContact(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("phone_number") phoneNumber: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendPoll")
    fun sendPoll(
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
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendChatAction")
    fun sendChatAction(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("action") action: ChatAction,
        @Field(ApiConstants.MESSAGE_THREAD_ID) messageThreadId: Long? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
    ): Call<Response<Boolean>>

    @GET("getUserProfilePhotos")
    fun getUserProfilePhotos(
        @Query(ApiConstants.USER_ID) userId: Long,
        @Query("offset") offset: Long?,
        @Query("limit") limit: Int? = null,
    ): Call<Response<UserProfilePhotos>>

    @GET("getFile")
    fun getFile(
        @Query("file_id") fileId: String,
    ): Call<Response<File>>

    @GET
    fun downloadFile(
        @Url customUrl: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("banChatMember")
    fun banChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("until_date") untilDate: Long?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("approveChatJoinRequest")
    fun approveChatJoinRequest(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("declineChatJoinRequest")
    fun declineChatJoinRequest(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("createChatInviteLink")
    fun createChatInviteLink(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("name") name: String?,
        @Field("expire_date") expireDate: Int?,
        @Field("member_limit") memberLimit: Int?,
        @Field("creates_join_request") createsJoinRequest: Boolean?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("editChatInviteLink")
    fun editChatInviteLink(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("invite_link") inviteLink: String?,
        @Field("name") name: String?,
        @Field("expire_date") expireDate: Int?,
        @Field("member_limit") memberLimit: Int?,
        @Field("creates_join_request") createsJoinRequest: Boolean?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("revokeChatInviteLink")
    fun revokeChatInviteLink(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("invite_link") inviteLink: String?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("unbanChatMember")
    fun unbanChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("only_if_banned") onlyIfBanned: Boolean?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("restrictChatMember")
    fun restrictChatMember(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("permissions") permissions: String,
        @Field("until_date") untilDate: Long?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("promoteChatMember")
    fun promoteChatMember(
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
        @Field("can_promote_members") canPromoteMembers: Boolean?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatPermissions")
    fun setChatPermissions(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("permissions") permissions: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("exportChatInviteLink")
    fun exportChatInviteLink(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<String>>

    @Multipart
    @POST("setChatPhoto")
    fun setChatPhoto(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part("photo") photo: MultipartBody.Part,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteChatPhoto")
    fun deleteChatPhoto(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatTitle")
    fun setChatTitle(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("title") title: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setChatDescription")
    fun setChatDescription(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("description") description: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("pinChatMessage")
    fun pinChatMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("unpinChatMessage")
    fun unpinChatMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("unpinAllChatMessages")
    fun unpinAllChatMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    // --- Forum topics (Bot API 6.3) ---

    @FormUrlEncoded
    @POST("createForumTopic")
    fun createForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("name") name: String,
        @Field("icon_color") iconColor: Int? = null,
        @Field("icon_custom_emoji_id") iconCustomEmojiId: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.ForumTopic>>

    @FormUrlEncoded
    @POST("editForumTopic")
    fun editForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_thread_id") messageThreadId: Long,
        @Field("name") name: String? = null,
        @Field("icon_custom_emoji_id") iconCustomEmojiId: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("closeForumTopic")
    fun closeForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_thread_id") messageThreadId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("reopenForumTopic")
    fun reopenForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_thread_id") messageThreadId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteForumTopic")
    fun deleteForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_thread_id") messageThreadId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("unpinAllForumTopicMessages")
    fun unpinAllForumTopicMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_thread_id") messageThreadId: Long,
    ): Call<Response<Boolean>>

    @GET("getForumTopicIconStickers")
    fun getForumTopicIconStickers(): Call<Response<List<com.github.kotlintelegrambot.entities.stickers.Sticker>>>

    // --- General forum topic (Bot API 6.4) ---

    @FormUrlEncoded
    @POST("editGeneralForumTopic")
    fun editGeneralForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("name") name: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("closeGeneralForumTopic")
    fun closeGeneralForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("reopenGeneralForumTopic")
    fun reopenGeneralForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("hideGeneralForumTopic")
    fun hideGeneralForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("unhideGeneralForumTopic")
    fun unhideGeneralForumTopic(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    // --- Batch forward/copy/delete (Bot API 7.0) ---

    @FormUrlEncoded
    @POST("forwardMessages")
    fun forwardMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field("message_ids") messageIds: String,
        @Field(ApiConstants.MESSAGE_THREAD_ID) messageThreadId: Long? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean? = null,
    ): Call<Response<List<com.github.kotlintelegrambot.entities.MessageId>>>

    @FormUrlEncoded
    @POST("copyMessages")
    fun copyMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("from_chat_id") fromChatId: ChatId,
        @Field("message_ids") messageIds: String,
        @Field(ApiConstants.MESSAGE_THREAD_ID) messageThreadId: Long? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean? = null,
        @Field("remove_caption") removeCaption: Boolean? = null,
    ): Call<Response<List<com.github.kotlintelegrambot.entities.MessageId>>>

    @FormUrlEncoded
    @POST("deleteMessages")
    fun deleteMessages(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_ids") messageIds: String,
    ): Call<Response<Boolean>>

    // --- Chat boosts (Bot API 7.0) ---

    @GET("getUserChatBoosts")
    fun getUserChatBoosts(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
        @Query("user_id") userId: Long,
    ): Call<Response<com.github.kotlintelegrambot.entities.UserChatBoosts>>

    // --- Bot info methods (Bot API 6.6 - 6.7) ---

    @FormUrlEncoded
    @POST("setMyDescription")
    fun setMyDescription(
        @Field("description") description: String? = null,
        @Field("language_code") languageCode: String? = null,
    ): Call<Response<Boolean>>

    @GET("getMyDescription")
    fun getMyDescription(
        @Query("language_code") languageCode: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.BotDescription>>

    @FormUrlEncoded
    @POST("setMyShortDescription")
    fun setMyShortDescription(
        @Field("short_description") shortDescription: String? = null,
        @Field("language_code") languageCode: String? = null,
    ): Call<Response<Boolean>>

    @GET("getMyShortDescription")
    fun getMyShortDescription(
        @Query("language_code") languageCode: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.BotShortDescription>>

    @FormUrlEncoded
    @POST("setMyName")
    fun setMyName(
        @Field("name") name: String? = null,
        @Field("language_code") languageCode: String? = null,
    ): Call<Response<Boolean>>

    @GET("getMyName")
    fun getMyName(
        @Query("language_code") languageCode: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.BotName>>

    @FormUrlEncoded
    @POST("setChatMenuButton")
    fun setChatMenuButton(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId? = null,
        @Field("menu_button") menuButton: String? = null,
    ): Call<Response<Boolean>>

    @GET("getChatMenuButton")
    fun getChatMenuButton(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.MenuButton>>

    @FormUrlEncoded
    @POST("setMyDefaultAdministratorRights")
    fun setMyDefaultAdministratorRights(
        @Field("rights") rights: String? = null,
        @Field("for_channels") forChannels: Boolean? = null,
    ): Call<Response<Boolean>>

    @GET("getMyDefaultAdministratorRights")
    fun getMyDefaultAdministratorRights(
        @Query("for_channels") forChannels: Boolean? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.ChatAdministratorRights>>

    @FormUrlEncoded
    @POST("leaveChat")
    fun leaveChat(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @GET("getChat")
    fun getChat(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<com.github.kotlintelegrambot.entities.ChatFullInfo>>

    @GET("getChatAdministrators")
    fun getChatAdministrators(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<List<ChatMember>>>

    @GET("getChatMemberCount")
    fun getChatMemberCount(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Int>>

    @GET("getChatMember")
    fun getChatMember(
        @Query(ApiConstants.CHAT_ID) chatId: ChatId,
        @Query(ApiConstants.USER_ID) userId: Long,
    ): Call<Response<ChatMember>>

    @FormUrlEncoded
    @POST("setChatStickerSet")
    fun setChatStickerSet(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("sticker_set_name") stickerSetName: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteChatStickerSet")
    fun deleteChatStickerSet(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerCallbackQuery")
    fun answerCallbackQuery(
        @Field("callback_query_id") callbackQueryId: String,
        @Field("text") text: String?,
        @Field("show_alert") showAlert: Boolean?,
        @Field("url") url: String?,
        @Field("cache_time") cacheTime: Int?,
    ): Call<Response<Boolean>>

    @GET("logOut")
    fun logOut(): Call<Response<Boolean>>

    @GET("close")
    fun close(): Call<Response<Boolean>>

    /**
     * Updating messages
     */

    @FormUrlEncoded
    @POST("editMessageText")
    fun editMessageText(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("text") text: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("entities") entities: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageCaption")
    fun editMessageCaption(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("caption") caption: String,
        @Field("parse_mode") parseMode: ParseMode?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("caption_entities") captionEntities: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageMedia")
    fun editMessageMedia(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field("media") media: InputMedia,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageReplyMarkup")
    fun editMessageReplyMarkup(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field("inline_message_id") inlineMessageId: String?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("stopPoll")
    fun stopPoll(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId?,
        @Field("message_id") messageId: Long?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
    ): Call<Response<Poll>>

    @FormUrlEncoded
    @POST("deleteMessage")
    fun deleteMessage(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
    ): Call<Response<Boolean>>

    /***
     * Stickers
     */

    @Multipart
    @POST("sendSticker")
    fun sendSticker(
        @Part(ApiConstants.CHAT_ID) chatId: ChatId,
        @Part("sticker") sticker: MultipartBody.Part,
        @Part(ApiConstants.DISABLE_NOTIFICATION) disableNotification: RequestBody?,
        @Part(ApiConstants.PROTECT_CONTENT) protectContent: RequestBody?,
        @Part(ApiConstants.REPLY_MARKUP) replyMarkup: RequestBody? = null,
        @Part("reply_parameters") replyParameters: RequestBody? = null,
        @Part("business_connection_id") businessConnectionId: RequestBody? = null,
        @Part("message_effect_id") messageEffectId: RequestBody? = null,
        @Part("allow_paid_broadcast") allowPaidBroadcast: RequestBody? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("sendSticker")
    fun sendSticker(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("sticker") fileId: String,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @GET("getStickerSet")
    fun getStickerSet(
        @Query("name") name: String,
    ): Call<Response<StickerSet>>

    @Multipart
    @POST("uploadStickerFile")
    fun uploadStickerFile(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part,
    ): Call<Response<File>>

    @Multipart
    @POST("createNewStickerSet")
    fun createNewStickerSet(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("title") title: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part,
        @Part("emojis") emojis: RequestBody,
        @Part("contains_masks") containsMasks: RequestBody?,
        @Part("mask_position") maskPosition: RequestBody?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("createNewStickerSet")
    fun createNewStickerSet(
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("name") name: String,
        @Field("title") title: String,
        @Field("png_sticker") fileId: String,
        @Field("emojis") emojis: String,
        @Field("contains_masks") containsMasks: Boolean?,
        @Field("mask_position") maskPosition: MaskPosition?,
    ): Call<Response<Boolean>>

    @Multipart
    @POST("addStickerToSet")
    fun addStickerToSet(
        @Part(ApiConstants.USER_ID) userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("png_sticker") pngSticker: MultipartBody.Part,
        @Part("emojis") emojis: RequestBody,
        @Part("mask_position") maskPosition: RequestBody?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("addStickerToSet")
    fun addStickerToSet(
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field("name") name: String,
        @Field("png_sticker") fileId: String,
        @Field("emojis") emojis: String,
        @Field("mask_position") maskPosition: MaskPosition?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setStickerPositionInSet")
    fun setStickerPositionInSet(
        @Part("sticker") sticker: String,
        @Part("position") position: Int,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteStickerFromSet")
    fun deleteStickerFromSet(
        @Part("sticker") sticker: String,
    ): Call<Response<Boolean>>

    /**
     * Payment
     */

    @FormUrlEncoded
    @POST("sendInvoice")
    fun sendInvoice(
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
        @Field("recurring") recurring: Boolean?,
        @Field("max_tip_amount") maxTipAmount: Long?,
        @Field("suggested_tip_amounts") suggestedTipAmounts: List<Long>?,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean?,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("answerShippingQuery")
    fun answerShippingQuery(
        @Field("shipping_query_id") shippingQueryId: String,
        @Field("ok") ok: Boolean,
        @Field("shipping_options") shippingOptions: List<ShippingOption>? = null,
        @Field("error_message") errorMessage: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerPreCheckoutQuery")
    fun answerPreCheckoutQuery(
        @Field("pre_checkout_query_id") preCheckoutQueryId: String,
        @Field("ok") ok: Boolean,
        @Field("error_message") errorMessage: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("refundStarPayment")
    fun refundStarPayment(
        @Field("user_id") userId: Long,
        @Field("telegram_payment_charge_id") telegramPaymentChargeId: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerInlineQuery")
    fun answerInlineQuery(
        @Field("inline_query_id") inlineQueryId: String,
        @Field("results") inlineQueryResults: String,
        @Field("cache_time") cacheTime: Int?,
        @Field("is_personal") isPersonal: Boolean,
        @Field("next_offset") nextOffset: String?,
        @Field("switch_pm_text") switchPmText: String?,
        @Field("switch_pm_parameter") switchPmParameter: String?,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("answerWebAppQuery")
    fun answerWebAppQuery(
        @Field("web_app_query_id") webAppQueryId: String,
        @Field("result") inlineQueryResult: String,
    ): Call<Response<SentWebAppMessage>>

    @GET("getMyCommands")
    fun getMyCommands(): Call<Response<List<BotCommand>>>

    @FormUrlEncoded
    @POST("setMyCommands")
    fun setMyCommands(
        @Field("commands") commands: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST(DiceFields.SEND_DICE_OP_NAME)
    fun sendDice(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(DiceFields.EMOJI) emoji: DiceEmoji? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean?,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST(ApiConstants.SetChatAdministratorCustomTitle.OP_NAME)
    fun setChatAdministratorCustomTitle(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field(ApiConstants.USER_ID) userId: Long,
        @Field(ApiConstants.SetChatAdministratorCustomTitle.CUSTOM_TITLE) customTitle: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setMessageReaction")
    fun setMessageReaction(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("reaction") reaction: String?,
        @Field("is_big") isBig: Boolean?,
    ): Call<Response<Boolean>>

    // --- Bot API 10.0: deleteMessageReaction / deleteAllMessageReactions ---

    @FormUrlEncoded
    @POST("deleteMessageReaction")
    fun deleteMessageReaction(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("user_id") userId: Long? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteAllMessageReactions")
    fun deleteAllMessageReactions(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
    ): Call<Response<Boolean>>

    // --- Bot API 7.5 / 7.6 — Stars and paid media ---

    @GET("getStarTransactions")
    fun getStarTransactions(
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Int? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.payments.StarTransactions>>

    @FormUrlEncoded
    @POST("sendPaidMedia")
    fun sendPaidMedia(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("star_count") starCount: Int,
        @Field("media") media: String,
        @Field("payload") payload: String? = null,
        @Field("caption") caption: String? = null,
        @Field("parse_mode") parseMode: ParseMode? = null,
        @Field("caption_entities") captionEntities: String? = null,
        @Field("show_caption_above_media") showCaptionAboveMedia: Boolean? = null,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field("business_connection_id") businessConnectionId: String? = null,
        @Field("allow_paid_broadcast") allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>>

    // --- Bot API 7.4 / 7.8 — Star payment refunds (refundStarPayment already exists) ---

    // --- Bot API 8.0 — Gifts ---

    @GET("getAvailableGifts")
    fun getAvailableGifts(): Call<Response<com.github.kotlintelegrambot.entities.gifts.Gifts>>

    @FormUrlEncoded
    @POST("sendGift")
    fun sendGift(
        @Field("user_id") userId: Long? = null,
        @Field(ApiConstants.CHAT_ID) chatId: ChatId? = null,
        @Field("gift_id") giftId: String,
        @Field("pay_for_upgrade") payForUpgrade: Boolean? = null,
        @Field("text") text: String? = null,
        @Field("text_parse_mode") textParseMode: ParseMode? = null,
        @Field("text_entities") textEntities: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("giftPremiumSubscription")
    fun giftPremiumSubscription(
        @Field("user_id") userId: Long,
        @Field("month_count") monthCount: Int,
        @Field("star_count") starCount: Int,
        @Field("text") text: String? = null,
        @Field("text_parse_mode") textParseMode: ParseMode? = null,
        @Field("text_entities") textEntities: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setUserEmojiStatus")
    fun setUserEmojiStatus(
        @Field("user_id") userId: Long,
        @Field("emoji_status_custom_emoji_id") emojiStatusCustomEmojiId: String? = null,
        @Field("emoji_status_expiration_date") emojiStatusExpirationDate: Long? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("savePreparedInlineMessage")
    fun savePreparedInlineMessage(
        @Field("user_id") userId: Long,
        @Field("result") result: String,
        @Field("allow_user_chats") allowUserChats: Boolean? = null,
        @Field("allow_bot_chats") allowBotChats: Boolean? = null,
        @Field("allow_group_chats") allowGroupChats: Boolean? = null,
        @Field("allow_channel_chats") allowChannelChats: Boolean? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.PreparedInlineMessage>>

    // --- Bot API 8.2 — Verification ---

    @FormUrlEncoded
    @POST("verifyUser")
    fun verifyUser(
        @Field("user_id") userId: Long,
        @Field("custom_description") customDescription: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("verifyChat")
    fun verifyChat(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("custom_description") customDescription: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("removeUserVerification")
    fun removeUserVerification(
        @Field("user_id") userId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("removeChatVerification")
    fun removeChatVerification(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
    ): Call<Response<Boolean>>

    // --- Bot API 9.0 — Business account management ---

    @FormUrlEncoded
    @POST("readBusinessMessage")
    fun readBusinessMessage(
        @Field("business_connection_id") businessConnectionId: String,
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("deleteBusinessMessages")
    fun deleteBusinessMessages(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("message_ids") messageIds: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setBusinessAccountName")
    fun setBusinessAccountName(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setBusinessAccountUsername")
    fun setBusinessAccountUsername(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("username") username: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setBusinessAccountBio")
    fun setBusinessAccountBio(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("bio") bio: String? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setBusinessAccountProfilePhoto")
    fun setBusinessAccountProfilePhoto(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("photo") photo: String,
        @Field("is_public") isPublic: Boolean? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("removeBusinessAccountProfilePhoto")
    fun removeBusinessAccountProfilePhoto(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("is_public") isPublic: Boolean? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("setBusinessAccountGiftSettings")
    fun setBusinessAccountGiftSettings(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("show_gift_button") showGiftButton: Boolean,
        @Field("accepted_gift_types") acceptedGiftTypes: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("getBusinessAccountStarBalance")
    fun getBusinessAccountStarBalance(
        @Field("business_connection_id") businessConnectionId: String,
    ): Call<Response<com.github.kotlintelegrambot.entities.payments.StarAmount>>

    @FormUrlEncoded
    @POST("transferBusinessAccountStars")
    fun transferBusinessAccountStars(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("star_count") starCount: Int,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("getBusinessAccountGifts")
    fun getBusinessAccountGifts(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("exclude_unsaved") excludeUnsaved: Boolean? = null,
        @Field("exclude_saved") excludeSaved: Boolean? = null,
        @Field("exclude_unlimited") excludeUnlimited: Boolean? = null,
        @Field("exclude_limited") excludeLimited: Boolean? = null,
        @Field("exclude_unique") excludeUnique: Boolean? = null,
        @Field("sort_by_price") sortByPrice: Boolean? = null,
        @Field("offset") offset: String? = null,
        @Field("limit") limit: Int? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.gifts.OwnedGifts>>

    @FormUrlEncoded
    @POST("convertGiftToStars")
    fun convertGiftToStars(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("owned_gift_id") ownedGiftId: String,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("upgradeGift")
    fun upgradeGift(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("owned_gift_id") ownedGiftId: String,
        @Field("keep_original_details") keepOriginalDetails: Boolean? = null,
        @Field("star_count") starCount: Int? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("transferGift")
    fun transferGift(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("owned_gift_id") ownedGiftId: String,
        @Field("new_owner_chat_id") newOwnerChatId: Long,
        @Field("star_count") starCount: Int? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("postStory")
    fun postStory(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("content") content: String,
        @Field("active_period") activePeriod: Int,
        @Field("caption") caption: String? = null,
        @Field("parse_mode") parseMode: ParseMode? = null,
        @Field("caption_entities") captionEntities: String? = null,
        @Field("areas") areas: String? = null,
        @Field("post_to_chat_page") postToChatPage: Boolean? = null,
        @Field("protect_content") protectContent: Boolean? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.Story>>

    @FormUrlEncoded
    @POST("editStory")
    fun editStory(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("story_id") storyId: Int,
        @Field("content") content: String,
        @Field("caption") caption: String? = null,
        @Field("parse_mode") parseMode: ParseMode? = null,
        @Field("caption_entities") captionEntities: String? = null,
        @Field("areas") areas: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.Story>>

    @FormUrlEncoded
    @POST("deleteStory")
    fun deleteStory(
        @Field("business_connection_id") businessConnectionId: String,
        @Field("story_id") storyId: Int,
    ): Call<Response<Boolean>>

    // --- Bot API 9.1 — Checklists ---

    @FormUrlEncoded
    @POST("sendChecklist")
    fun sendChecklist(
        @Field("business_connection_id") businessConnectionId: String,
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("checklist") checklist: String,
        @Field(ApiConstants.DISABLE_NOTIFICATION) disableNotification: Boolean? = null,
        @Field(ApiConstants.PROTECT_CONTENT) protectContent: Boolean? = null,
        @Field("message_effect_id") messageEffectId: String? = null,
        @Field("reply_parameters") replyParameters: String? = null,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
    ): Call<Response<Message>>

    @FormUrlEncoded
    @POST("editMessageChecklist")
    fun editMessageChecklist(
        @Field("business_connection_id") businessConnectionId: String,
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("checklist") checklist: String,
        @Field(ApiConstants.REPLY_MARKUP) replyMarkup: ReplyMarkup? = null,
    ): Call<Response<Message>>

    @GET("getMyStarBalance")
    fun getMyStarBalance(): Call<Response<com.github.kotlintelegrambot.entities.payments.StarAmount>>

    // --- Bot API 9.2 — Suggested posts ---

    @FormUrlEncoded
    @POST("approveSuggestedPost")
    fun approveSuggestedPost(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("send_date") sendDate: Long? = null,
    ): Call<Response<Boolean>>

    @FormUrlEncoded
    @POST("declineSuggestedPost")
    fun declineSuggestedPost(
        @Field(ApiConstants.CHAT_ID) chatId: ChatId,
        @Field("message_id") messageId: Long,
        @Field("comment") comment: String? = null,
    ): Call<Response<Boolean>>

    // --- Bot API 10.0 — Guest mode ---

    @FormUrlEncoded
    @POST("answerGuestQuery")
    fun answerGuestQuery(
        @Field("guest_query_id") guestQueryId: String,
        @Field("text") text: String? = null,
        @Field("parse_mode") parseMode: ParseMode? = null,
        @Field("entities") entities: String? = null,
    ): Call<Response<com.github.kotlintelegrambot.entities.guest.SentGuestMessage>>
}

class LabeledPriceList(private val labeledPrice: List<LabeledPrice>) {

    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String {
        return GSON.toJson(labeledPrice)
    }
}
