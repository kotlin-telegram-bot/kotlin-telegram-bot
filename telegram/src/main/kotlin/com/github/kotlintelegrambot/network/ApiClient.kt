package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByFileId
import com.github.kotlintelegrambot.entities.TelegramFile.ByUrl
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.UserProfilePhotos
import com.github.kotlintelegrambot.entities.WebhookInfo
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.files.File
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.entities.payments.LabeledPrice
import com.github.kotlintelegrambot.entities.payments.ShippingOption
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollType
import com.github.kotlintelegrambot.entities.stickers.MaskPosition
import com.github.kotlintelegrambot.entities.stickers.StickerSet
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.logging.toOkHttpLogLevel
import com.github.kotlintelegrambot.network.multipart.MultipartBodyFactory
import com.github.kotlintelegrambot.network.multipart.toMultipartBodyPart
import com.github.kotlintelegrambot.network.retrofit.converters.DiceEmojiConverterFactory
import com.github.kotlintelegrambot.network.retrofit.converters.EnumRetrofitConverterFactory
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File as SystemFile
import java.net.Proxy
import java.nio.file.Files
import java.util.Date
import java.util.concurrent.TimeUnit
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val PLAIN_TEXT_MIME = MediaType.parse("text/plain")
private val APPLICATION_JSON_MIME = MediaType.parse("application/json")

private fun convertString(text: String) = RequestBody.create(PLAIN_TEXT_MIME, text)
private fun convertJson(text: String) = RequestBody.create(APPLICATION_JSON_MIME, text)

private fun convertFile(
    name: String,
    file: SystemFile,
    mimeType: String? = null
): MultipartBody.Part {
    val mediaType = (mimeType ?: Files.probeContentType(file.toPath()))?.let { MediaType.parse(it) }
    val requestBody = RequestBody.create(mediaType, file)

    return MultipartBody.Part.createFormData(name, file.name, requestBody)
}

private fun convertBytes(
    name: String,
    bytes: ByteArray,
    mimeType: String? = null
): MultipartBody.Part {
    val mediaType = mimeType?.let { MediaType.parse(it) }
    val requestBody = RequestBody.create(mediaType, bytes)

    return MultipartBody.Part.createFormData(name, name, requestBody)
}

private fun ByteArray.toMultipartBodyPart(
    name: String,
    filename: String,
    mimeType: String? = null
): MultipartBody.Part {
    val mediaType = mimeType?.let { MediaType.parse(it) }
    val requestBody = RequestBody.create(mediaType, this)

    return MultipartBody.Part.createFormData(name, filename, requestBody)
}

class ApiClient(
    private val token: String,
    private val apiUrl: String,
    private val botTimeout: Int = 30,
    logLevel: LogLevel,
    proxy: Proxy = Proxy.NO_PROXY,
    private val gson: Gson = GsonFactory.createForApiClient(),
    private val multipartBodyFactory: MultipartBodyFactory = MultipartBodyFactory(GsonFactory.createForMultipartBodyFactory())
) {

    private val service: ApiService

    // TODO check if init is the best approach for this
    init {
        val logging = HttpLoggingInterceptor().apply { level = logLevel.toOkHttpLogLevel() }

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .readTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .writeTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .proxy(proxy)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("${apiUrl}bot$token/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            // In retrofit, Gson is used only for response/request decoding/encoding, but not for @Query/@Url/@Path etc...
            // For them, Retrofit uses Converter.Factory classes to convert any type to String. By default, enums are transformed
            // with BuiltInConverters.ToStringConverter which just calls to the toString() method of a given object.
            // Is needed to provide a special Converter.Factory if a custom transformation is wanted for them.
            .addConverterFactory(EnumRetrofitConverterFactory())
            .addConverterFactory(DiceEmojiConverterFactory())
            .build()

        service = retrofit.create(ApiService::class.java)
    }

    fun getUpdates(
        offset: Long? = null,
        limit: Int? = null,
        timeout: Int? = botTimeout
    ): Call<Response<List<Update>>> {
        return service.getUpdates(offset, limit, timeout)
    }

    fun setWebhook(
        url: String,
        certificate: TelegramFile? = null,
        maxConnections: Int? = null,
        allowedUpdates: List<String>? = null
    ): Call<Response<Boolean>> = when (certificate) {
        is ByFileId -> service.setWebhookWithCertificateAsFileId(
            url = url,
            certificateFileId = certificate.fileId,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
        is ByUrl -> service.setWebhookWithCertificateAsFileUrl(
            url = url,
            certificateUrl = certificate.url,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
        is ByFile -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.file.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                mediaType = MediaTypeConstants.UTF_8_TEXT
            ),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES)
        )
        null -> service.setWebhook(
            url = url,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
    }

    fun deleteWebhook(): Call<Response<Boolean>> = service.deleteWebhook()

    fun getWebhookInfo(): Call<Response<WebhookInfo>> = service.getWebhookInfo()

    /**
     * Available methods
     */
    fun getMe(): Call<Response<User>> {
        return service.getMe()
    }

    fun sendMessage(
        chatId: Long,
        text: String,
        parseMode: String?,
        disableWebPagePreview: Boolean?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendMessage(
            chatId, text, parseMode, disableWebPagePreview, disableNotification,
            replyToMessageId, replyMarkup
        )
    }

    fun forwardMessage(
        chatId: Long,
        fromChatId: Long,
        messageId: Long,
        disableNotification: Boolean?
    ): Call<Response<Message>> {

        return service.forwardMessage(chatId, fromChatId, disableNotification, messageId)
    }

    fun sendPhoto(
        chatId: Long,
        photo: SystemFile,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendPhoto(
            convertString(chatId.toString()),
            convertFile("photo", photo),
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendPhoto(
        chatId: Long,
        photo: String,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendPhoto(
            chatId,
            photo,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendAudio(
        chatId: Long,
        audio: SystemFile,
        duration: Int?,
        performer: String?,
        title: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendAudio(
            convertString(chatId.toString()),
            convertFile("audio", audio),
            if (duration != null) convertString(duration.toString()) else null,
            if (performer != null) convertString(performer) else null,
            if (title != null) convertString(title) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendAudio(
        chatId: Long,
        audio: String,
        duration: Int?,
        performer: String?,
        title: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendAudio(
            chatId,
            audio,
            duration,
            performer,
            title,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendDocument(
        chatId: Long,
        document: SystemFile,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendDocument(
            convertString(chatId.toString()),
            convertFile("document", document),
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendDocument(
        chatId: Long,
        fileId: String,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendDocument(
            chatId,
            fileId,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendDocument(
        chatId: Long,
        fileBytes: ByteArray,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?,
        filename: String
    ): Call<Response<Message>> {

        return service.sendDocument(
            convertString(chatId.toString()),
            fileBytes.toMultipartBodyPart(name = "document", filename = filename),
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendVideo(
        chatId: Long,
        video: SystemFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVideo(
            convertString(chatId.toString()),
            convertFile("video", video),
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendVideo(
        chatId: Long,
        fileId: String,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVideo(
            chatId,
            fileId,
            duration,
            width,
            height,
            caption,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendAnimation(
        chatId: Long,
        animation: SystemFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendAnimation(
            convertString(chatId.toString()),
            convertFile("video", animation),
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendAnimation(
        chatId: Long,
        fileId: String,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendAnimation(
            chatId,
            fileId,
            duration,
            width,
            height,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendVoice(
        chatId: Long,
        audio: SystemFile,
        duration: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVoice(
            convertString(chatId.toString()),
            convertFile("voice", audio),
            if (duration != null) convertString(duration.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendVoice(
        chatId: Long,
        audioId: String,
        duration: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVoice(
            chatId,
            audioId,
            duration,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendVoice(
        chatId: Long,
        audio: ByteArray,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> {

        return service.sendVoice(
            convertString(chatId.toString()),
            convertBytes("voice", audio),
            if (duration != null) convertString(duration.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendVideoNote(
        chatId: Long,
        videoNote: SystemFile,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVideoNote(
            convertString(chatId.toString()),
            convertFile("video_note", videoNote),
            if (duration != null) convertString(duration.toString()) else null,
            if (length != null) convertString(length.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendVideoNote(
        chatId: Long,
        videoNoteId: String,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVideoNote(
            chatId,
            videoNoteId,
            duration,
            length,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    /**
     * Use this method to send a group of photos or videos as an album
     * @param chatId Unique identifier for the target chat
     * @param mediaGroup An object describing photos and videos to be sent, must include 2-10 items
     * @param disableNotification Sends the messages silently. Users will receive a notification with no sound
     * @param replyToMessageId If the messages are a reply, ID of the original message
     * @return an array of the sent Messages
     */
    fun sendMediaGroup(
        chatId: Long,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): Call<Response<Array<Message>>> {
        val sendMediaGroupMultipartBody = multipartBodyFactory.createForSendMediaGroup(
            chatId,
            mediaGroup,
            disableNotification,
            replyToMessageId
        )
        return service.sendMediaGroup(sendMediaGroupMultipartBody)
    }

    /**
     * Use this method to send a group of photos or videos as an album
     * @param chatId Username of the target channel (in the format @channelusername)
     * @param mediaGroup An object describing photos and videos to be sent, must include 2-10 items
     * @param disableNotification Sends the messages silently. Users will receive a notification with no sound
     * @param replyToMessageId If the messages are a reply, ID of the original message
     * @return an array of the sent Messages
     */
    fun sendMediaGroup(
        chatId: String,
        mediaGroup: MediaGroup,
        disableNotification: Boolean?,
        replyToMessageId: Long?
    ): Call<Response<Array<Message>>> {
        val sendMediaGroupMultipartBody = multipartBodyFactory.createForSendMediaGroup(
            chatId,
            mediaGroup,
            disableNotification,
            replyToMessageId
        )
        return service.sendMediaGroup(sendMediaGroupMultipartBody)
    }

    fun sendLocation(
        chatId: Long,
        latitude: Float,
        longitude: Float,
        livePeriod: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendLocation(
            chatId,
            latitude,
            longitude,
            livePeriod,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun editMessageLiveLocation(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            latitude,
            longitude,
            replyMarkup
        )
    }

    fun stopMessageLiveLocation(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.stopMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    fun sendVenue(
        chatId: Long,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String?,
        foursquareType: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendVenue(
            chatId,
            latitude,
            longitude,
            title,
            address,
            foursquareId,
            foursquareType,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendContact(
        chatId: Long,
        phoneNumber: String,
        firstName: String,
        lastName: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendContact(
            chatId,
            phoneNumber,
            firstName,
            lastName,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun sendPoll(
        chatId: Long,
        question: String,
        options: List<String>,
        isAnonymous: Boolean? = null,
        type: PollType? = null,
        allowsMultipleAnswers: Boolean? = null,
        correctOptionId: Int? = null,
        explanation: String? = null,
        explanationParseMode: ParseMode? = null,
        openPeriod: Int? = null,
        closeDate: Long? = null,
        isClosed: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> = service.sendPoll(
        chatId,
        question,
        gson.toJson(options),
        isAnonymous,
        type,
        allowsMultipleAnswers,
        correctOptionId,
        explanation,
        explanationParseMode,
        openPeriod,
        closeDate,
        isClosed,
        disableNotification,
        replyToMessageId,
        replyMarkup
    )

    fun sendPoll(
        channelUsername: String,
        question: String,
        options: List<String>,
        isAnonymous: Boolean? = null,
        type: PollType? = null,
        allowsMultipleAnswers: Boolean? = null,
        correctOptionId: Int? = null,
        explanation: String? = null,
        explanationParseMode: ParseMode? = null,
        openPeriod: Int? = null,
        closeDate: Long? = null,
        isClosed: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> = service.sendPoll(
        channelUsername,
        question,
        gson.toJson(options),
        isAnonymous,
        type,
        allowsMultipleAnswers,
        correctOptionId,
        explanation,
        explanationParseMode,
        openPeriod,
        closeDate,
        isClosed,
        disableNotification,
        replyToMessageId,
        replyMarkup
    )

    fun sendChatAction(chatId: Long, action: ChatAction): Call<Response<Boolean>> {

        return service.sendChatAction(chatId, action)
    }

    fun getUserProfilePhotos(
        userId: Long,
        offset: Long?,
        limit: Int?
    ): Call<Response<UserProfilePhotos>> {

        return service.getUserProfilePhotos(userId, offset, limit)
    }

    fun getFile(fileId: String): Call<Response<File>> {

        return service.getFile(fileId)
    }

    fun downloadFile(filePath: String): Call<ResponseBody> {
        return service.downloadFile("${apiUrl}file/bot$token/$filePath")
    }

    fun kickChatMember(chatId: Long, userId: Long, untilDate: Date): Call<Response<Boolean>> {

        return service.kickChatMember(chatId, userId, untilDate)
    }

    fun unbanChatMember(chatId: Long, userId: Long): Call<Response<Boolean>> {

        return service.unbanChatMember(chatId, userId)
    }

    fun restrictChatMember(
        chatId: Long,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Date?
    ): Call<Response<Boolean>> {

        return service.restrictChatMember(
            chatId,
            userId,
            chatPermissions,
            untilDate
        )
    }

    fun promoteChatMember(
        chatId: Long,
        userId: Long,
        canChangeInfo: Boolean?,
        canPostMessages: Boolean?,
        canEditMessages: Boolean?,
        canDeleteMessages: Boolean?,
        canInviteUsers: Boolean?,
        canRestrictMembers: Boolean?,
        canPinMessages: Boolean?,
        canPromoteMembers: Boolean?
    ): Call<Response<Boolean>> {

        return service.promoteChatMember(
            chatId,
            userId,
            canChangeInfo,
            canPostMessages,
            canEditMessages,
            canDeleteMessages,
            canInviteUsers,
            canRestrictMembers,
            canPinMessages,
            canPromoteMembers
        )
    }

    fun setChatPermissions(chatId: Long, permissions: ChatPermissions): Call<Response<Boolean>> {

        return service.setChatPermissions(chatId, permissions)
    }

    fun exportChatInviteLink(chatId: Long): Call<Response<String>> {

        return service.exportChatInviteLink(chatId)
    }

    fun setChatPhoto(
        chatId: Long,
        photo: SystemFile
    ): Call<Response<Boolean>> {
        return service.setChatPhoto(convertString(chatId.toString()), convertFile("photo", photo))
    }

    fun deleteChatPhoto(chatId: Long): Call<Response<Boolean>> {

        return service.deleteChatPhoto(chatId)
    }

    fun setChatTitle(chatId: Long, title: String): Call<Response<Boolean>> {

        return service.setChatTitle(chatId, title)
    }

    fun setChatDescription(chatId: Long, description: String): Call<Response<Boolean>> {

        return service.setChatDescription(chatId, description)
    }

    fun pinChatMessage(
        chatId: Long,
        messageId: Long,
        disableNotification: Boolean?
    ): Call<Response<Boolean>> {

        return service.pinChatMessage(chatId, messageId, disableNotification)
    }

    fun unpinChatMessage(chatId: Long): Call<Response<Boolean>> {

        return service.unpinChatMessage(chatId)
    }

    fun leaveChat(chatId: Long): Call<Response<Boolean>> {

        return service.leaveChat(chatId)
    }

    fun getChat(chatId: Long): Call<Response<Chat>> {

        return service.getChat(chatId)
    }

    fun getChatAdministrators(chatId: Long): Call<Response<List<ChatMember>>> {

        return service.getChatAdministrators(chatId)
    }

    fun getChatMembersCount(chatId: Long): Call<Response<Int>> {

        return service.getChatMembersCount(chatId)
    }

    fun getChatMember(chatId: Long, userId: Long): Call<Response<ChatMember>> {

        return service.getChatMember(chatId, userId)
    }

    fun setChatStickerSet(chatId: Long, stickerSetName: String): Call<Response<Boolean>> {

        return service.setChatStickerSet(chatId, stickerSetName)
    }

    fun deleteChatStickerSet(chatId: Long): Call<Response<Boolean>> {

        return service.deleteChatStickerSet(chatId)
    }

    fun answerCallbackQuery(
        callbackQueryId: String,
        text: String?,
        showAlert: Boolean?,
        url: String?,
        cacheTime: Int?
    ): Call<Response<Boolean>> {

        return service.answerCallbackQuery(
            callbackQueryId,
            text,
            showAlert,
            url,
            cacheTime
        )
    }

    /**
     * Updating messages
     */

    fun editMessageText(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        text: String,
        parseMode: String?,
        disableWebPagePreview: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageText(
            chatId,
            messageId,
            inlineMessageId,
            text,
            parseMode,
            disableWebPagePreview,
            replyMarkup
        )
    }

    fun editMessageCaption(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        caption: String,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageCaption(
            chatId,
            messageId,
            inlineMessageId,
            caption,
            replyMarkup
        )
    }

    fun editMessageMedia(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        media: InputMedia,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageMedia(
            chatId,
            messageId,
            inlineMessageId,
            media,
            replyMarkup
        )
    }

    fun editMessageReplyMarkup(
        chatId: Long?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageReplyMarkup(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    fun stopPoll(
        chatId: Long?,
        messageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Poll>> {

        return service.stopPoll(
            chatId,
            messageId,
            replyMarkup
        )
    }

    fun deleteMessage(chatId: Long?, messageId: Long?): Call<Response<Message>> {

        return service.deleteMessage(chatId, messageId)
    }

    /**
     * Payment
     */

    fun sendInvoice(
        chatId: Long,
        title: String,
        description: String,
        payload: String,
        providerToken: String,
        startParameter: String,
        currency: String,
        prices: List<LabeledPrice>,
        providerData: String?,
        photoUrl: String?,
        photoSize: Int?,
        photoWidth: Int?,
        photoHeight: Int?,
        needName: Boolean?,
        needPhoneNumber: Boolean?,
        needEmail: Boolean?,
        needShippingAddress: Boolean?,
        sendPhoneNumberToProvider: Boolean?,
        sendEmailToProvider: Boolean?,
        isFlexible: Boolean?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: InlineKeyboardMarkup?
    ): Call<Response<Message>> {
        return service.sendInvoice(
            chatId = chatId,
            title = title,
            description = description,
            payload = payload,
            providerToken = providerToken,
            startParameter = startParameter,
            currency = currency,
            prices = LabeledPriceList(prices),
            providerData = providerData,
            photoHeight = photoHeight,
            photoSize = photoSize,
            photoUrl = photoUrl,
            photoWidth = photoWidth,
            needEmail = needEmail,
            needName = needName,
            needPhoneNumber = needPhoneNumber,
            needShippingAddress = needShippingAddress,
            sendPhoneNumberToProvider = sendPhoneNumberToProvider,
            sendEmailToProvider = sendEmailToProvider,
            isFlexible = isFlexible,
            disableNotification = disableNotification,
            replyMarkup = replyMarkup,
            replyToMessageId = replyToMessageId
        )
    }

    fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>?,
        errorMessage: String?
    ) = service.answerShippingQuery(shippingQueryId, ok, shippingOptions, errorMessage)

    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String?
    ) = service.answerPreCheckoutQuery(preCheckoutQueryId, ok, errorMessage)

    /***
     * Stickers
     */

    fun sendSticker(
        chatId: Long,
        sticker: SystemFile,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendSticker(
            convertString(chatId.toString()),
            convertFile("photo", sticker),
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendSticker(
        chatId: Long,
        sticker: String,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendSticker(
            chatId,
            sticker,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

    fun getStickerSet(
        name: String
    ): Call<Response<StickerSet>> {

        return service.getStickerSet(name)
    }

    fun uploadStickerFile(
        userId: Long,
        pngSticker: SystemFile
    ): Call<Response<File>> {

        return service.uploadStickerFile(
            convertString(userId.toString()),
            convertFile("photo", pngSticker)
        )
    }

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: SystemFile,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?
    ): Call<Response<Boolean>> {

        return service.createNewStickerSet(
            convertString(userId.toString()),
            convertString(name),
            convertString(title),
            convertFile("photo", pngSticker),
            convertString(emojis),
            if (containsMasks != null) convertString(containsMasks.toString()) else null,
            if (maskPosition != null) convertJson(maskPosition.toString()) else null
        )
    }

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: String,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?
    ): Call<Response<Boolean>> {

        return service.createNewStickerSet(
            userId,
            name,
            title,
            pngSticker,
            emojis,
            containsMasks,
            maskPosition
        )
    }

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: SystemFile,
        emojis: String,
        maskPosition: MaskPosition?
    ): Call<Response<Boolean>> {

        return service.addStickerToSet(
            convertString(userId.toString()),
            convertString(name),
            convertFile("photo", pngSticker),
            convertString(emojis),
            if (maskPosition != null) convertJson(maskPosition.toString()) else null
        )
    }

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: String,
        emojis: String,
        maskPosition: MaskPosition?
    ): Call<Response<Boolean>> {

        return service.addStickerToSet(
            userId,
            name,
            pngSticker,
            emojis,
            maskPosition
        )
    }

    fun setStickerPositionInSet(
        sticker: String,
        position: Int
    ): Call<Response<Boolean>> {

        return service.setStickerPositionInSet(
            sticker,
            position
        )
    }

    fun deleteStickerFromSet(
        sticker: String
    ): Call<Response<Boolean>> {

        return service.deleteStickerFromSet(
            sticker
        )
    }

    fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int?,
        isPersonal: Boolean,
        nextOffset: String?,
        switchPmText: String?,
        switchPmParameter: String?
    ): Call<Response<Boolean>> {
        val inlineQueryResultsType = object : TypeToken<List<InlineQueryResult>>() {}.type
        val serializedInlineQueryResults = gson.toJson(inlineQueryResults, inlineQueryResultsType)

        return service.answerInlineQuery(
            inlineQueryId,
            serializedInlineQueryResults,
            cacheTime,
            isPersonal,
            nextOffset,
            switchPmText,
            switchPmParameter
        )
    }

    fun getMyCommands(): Call<Response<List<BotCommand>>> {
        return service.getMyCommands()
    }

    fun setMyCommands(commands: List<BotCommand>): Call<Response<Boolean>> {
        return service.setMyCommands(gson.toJson(commands))
    }

    fun sendDice(
        chatId: Long,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> = service.sendDice(
        chatId,
        emoji,
        disableNotification,
        replyToMessageId,
        replyMarkup
    )

    fun sendDice(
        channelUsername: String,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> = service.sendDice(
        channelUsername,
        emoji,
        disableNotification,
        replyToMessageId,
        replyMarkup
    )

    fun setChatAdministratorCustomTitle(
        chatId: Long,
        userId: Long,
        customTitle: String
    ): Call<Response<Boolean>> = service.setChatAdministratorCustomTitle(
        chatId,
        userId,
        customTitle
    )

    fun setChatAdministratorCustomTitle(
        channelUsername: String,
        userId: Long,
        customTitle: String
    ): Call<Response<Boolean>> = service.setChatAdministratorCustomTitle(
        channelUsername,
        userId,
        customTitle
    )
}
