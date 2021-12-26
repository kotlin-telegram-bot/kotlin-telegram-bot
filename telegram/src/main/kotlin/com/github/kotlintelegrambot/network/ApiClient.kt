package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByByteArray
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
import com.github.kotlintelegrambot.network.MediaTypeConstants.AUDIO_OGG
import com.github.kotlintelegrambot.network.multipart.MultipartBodyFactory
import com.github.kotlintelegrambot.network.multipart.toMultipartBodyPart
import com.github.kotlintelegrambot.network.retrofit.converters.ChatIdConverterFactory
import com.github.kotlintelegrambot.network.retrofit.converters.DiceEmojiConverterFactory
import com.github.kotlintelegrambot.network.retrofit.converters.EnumRetrofitConverterFactory
import com.github.kotlintelegrambot.network.retrofit.converters.InputMediaConverterFactory
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.github.kotlintelegrambot.types.TelegramBotResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit
import java.io.File as SystemFile

internal val PLAIN_TEXT_MIME = "text/plain".toMediaTypeOrNull()
internal val APPLICATION_JSON_MIME = "application/json".toMediaTypeOrNull()

private fun convertString(text: String) = text.toRequestBody(PLAIN_TEXT_MIME)
private fun convertJson(text: String) = text.toRequestBody(APPLICATION_JSON_MIME)

internal class ApiClient(
    private val token: String,
    private val apiUrl: String,
    botTimeout: Int = 30,
    logLevel: LogLevel,
    proxy: Proxy = Proxy.NO_PROXY,
    private val gson: Gson,
    private val multipartBodyFactory: MultipartBodyFactory = MultipartBodyFactory(GsonFactory.createForMultipartBodyFactory()),
    private val apiResponseMapper: ApiResponseMapper = ApiResponseMapper()
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
            .addConverterFactory(ChatIdConverterFactory())
            // In retrofit, Gson is used only for response/request decoding/encoding, but not for @Query/@Url/@Path etc...
            // For them, Retrofit uses Converter.Factory classes to convert any type to String. By default, enums are transformed
            // with BuiltInConverters.ToStringConverter which just calls to the toString() method of a given object.
            // Is needed to provide a special Converter.Factory if a custom transformation is wanted for them.
            .addConverterFactory(EnumRetrofitConverterFactory())
            .addConverterFactory(DiceEmojiConverterFactory())
            .addConverterFactory(InputMediaConverterFactory(gson))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        service = retrofit.create(ApiService::class.java)
    }

    suspend fun getUpdates(
        offset: Long?,
        limit: Int?,
        timeout: Int?,
        allowedUpdates: List<String>?
    ): TelegramBotResult<List<Update>> = runApiOperation {
        service.getUpdates(
            offset,
            limit,
            timeout,
            allowedUpdates?.serialize(),
        )
    }

    suspend fun setWebhook(
        url: String,
        certificate: TelegramFile? = null,
        ipAddress: String? = null,
        maxConnections: Int? = null,
        allowedUpdates: List<String>? = null
    ): CallResponse<Response<Boolean>> = when (certificate) {
        is ByFileId -> service.setWebhookWithCertificateAsFileId(
            url = url,
            certificateFileId = certificate.fileId,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
        is ByUrl -> service.setWebhookWithCertificateAsFileUrl(
            url = url,
            certificateUrl = certificate.url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
        is ByFile -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.file.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                mediaType = MediaTypeConstants.UTF_8_TEXT
            ),
            ipAddress = ipAddress?.toMultipartBodyPart(ApiConstants.SetWebhook.IP_ADDRESS),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES)
        )
        is ByByteArray -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.fileBytes.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                filename = certificate.filename,
                mediaType = MediaTypeConstants.UTF_8_TEXT
            ),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES)
        )
        null -> service.setWebhook(
            url = url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates
        )
    }

    suspend fun deleteWebhook(): CallResponse<Response<Boolean>> = service.deleteWebhook()

    suspend fun getWebhookInfo(): CallResponse<Response<WebhookInfo>> = service.getWebhookInfo()

    suspend fun getMe(): TelegramBotResult<User> = runApiOperation { service.getMe() }

    suspend fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode?,
        disableWebPagePreview: Boolean?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): TelegramBotResult<Message> = runApiOperation {
        service.sendMessage(
            chatId,
            text,
            parseMode,
            disableWebPagePreview,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        disableNotification: Boolean?
    ): TelegramBotResult<Message> = runApiOperation {
        service.forwardMessage(
            chatId,
            fromChatId,
            disableNotification,
            messageId,
        )
    }

    suspend fun copyMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): CallResponse<Response<MessageId>> {
        return service.copyMessage(
            chatId,
            fromChatId,
            messageId,
            caption,
            parseMode,
            if (captionEntities != null) gson.toJson(captionEntities) else null,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendPhoto(
        chatId: ChatId,
        photo: TelegramFile,
        caption: String?,
        parseMode: ParseMode?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> = when (photo) {
        is ByFile, is ByByteArray -> service.sendPhoto(
            chatId,
            when (photo) {
                is ByFile -> photo.file.toMultipartBodyPart("photo")
                is ByByteArray -> photo.fileBytes.toMultipartBodyPart("photo", photo.filename)
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendPhoto(
            chatId,
            when (photo) {
                is ByFileId -> photo.fileId
                is ByUrl -> photo.url
                else -> throw NotImplementedError() // KT-31622
            },
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendAudio(
        chatId: ChatId,
        audio: TelegramFile,
        duration: Int?,
        performer: String?,
        title: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> = when (audio) {
        is ByFile, is ByByteArray -> service.sendAudio(
            chatId,
            when (audio) {
                is ByFile -> audio.file.toMultipartBodyPart("audio")
                is ByByteArray -> audio.fileBytes.toMultipartBodyPart("audio", audio.filename)
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (performer != null) convertString(performer) else null,
            if (title != null) convertString(title) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendAudio(
            chatId,
            when (audio) {
                is ByFileId -> audio.fileId
                is ByUrl -> audio.url
                else -> throw NotImplementedError() // KT-31622
            },
            duration,
            performer,
            title,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendDocument(
        chatId: ChatId,
        document: TelegramFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        mimeType: String? = null
    ): CallResponse<Response<Message>> = when (document) {
        is ByFile, is ByByteArray -> service.sendDocument(
            chatId,
            when (document) {
                is ByFile -> document.file.toMultipartBodyPart("document", mimeType)
                is ByByteArray -> document.fileBytes.toMultipartBodyPart(
                    "document",
                    document.filename,
                    mimeType
                )
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableContentTypeDetection != null) convertString(disableContentTypeDetection.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendDocument(
            chatId,
            when (document) {
                is ByFileId -> document.fileId
                is ByUrl -> document.url
                else -> throw NotImplementedError() // KT-31622
            },
            caption,
            parseMode,
            disableContentTypeDetection,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendVideo(
        chatId: ChatId,
        video: TelegramFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> = when (video) {
        is ByFile, is ByByteArray -> service.sendVideo(
            chatId,
            when (video) {
                is ByFile -> video.file.toMultipartBodyPart("video")
                is ByByteArray -> video.fileBytes.toMultipartBodyPart("video", video.filename)
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendVideo(
            chatId,
            when (video) {
                is ByFileId -> video.fileId
                is ByUrl -> video.url
                else -> throw NotImplementedError() // KT-31622
            },
            duration,
            width,
            height,
            caption,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendGame(
        chatId: ChatId,
        gameShortName: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): TelegramBotResult<Message> = runApiOperation {
        service.sendGame(
            chatId,
            gameShortName,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated("Use overloaded version instead")
    suspend fun sendAnimation(
        chatId: ChatId,
        animation: SystemFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        parseMode: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendAnimation(
            chatId,
            animation.toMultipartBodyPart("video"),
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    suspend fun sendAnimation(
        chatId: ChatId,
        animation: TelegramFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        parseMode: ParseMode?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> = when (animation) {
        is ByFile, is ByByteArray -> service.sendAnimation(
            chatId,
            when (animation) {
                is ByFile -> animation.file.toMultipartBodyPart("video")
                is ByByteArray -> animation.fileBytes.toMultipartBodyPart(
                    "video",
                    animation.filename
                )
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendAnimation(
            chatId,
            when (animation) {
                is ByFileId -> animation.fileId
                is ByUrl -> animation.url
                else -> throw NotImplementedError() // KT-31622
            },
            duration,
            width,
            height,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendVoice(
        chatId: ChatId,
        audio: TelegramFile,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        duration: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> = when (audio) {
        is ByFile, is ByByteArray -> service.sendVoice(
            chatId,
            when (audio) {
                is ByFile -> audio.file.toMultipartBodyPart("voice", AUDIO_OGG)
                is ByByteArray -> audio.fileBytes.toMultipartBodyPart(
                    "voice",
                    audio.filename,
                    AUDIO_OGG
                )
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (captionEntities != null) convertJson(gson.toJson(captionEntities)) else null,
            if (duration != null) convertString(duration.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
        is ByFileId, is ByUrl -> service.sendVoice(
            chatId,
            when (audio) {
                is ByFileId -> audio.fileId
                is ByUrl -> audio.url
                else -> throw NotImplementedError() // KT-31622
            },
            caption,
            parseMode,
            if (captionEntities != null) gson.toJson(captionEntities) else null,
            duration,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: ByFile,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendVideoNote(
            chatId,
            videoNote.file.toMultipartBodyPart("video_note"),
            if (duration != null) convertString(duration.toString()) else null,
            if (length != null) convertString(length.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    suspend fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: ByFileId,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendVideoNote(
            chatId,
            videoNoteId.fileId,
            duration,
            length,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null
    ): TelegramBotResult<List<Message>> = runApiOperation {
        val sendMediaGroupMultipartBody = multipartBodyFactory.createForSendMediaGroup(
            chatId,
            mediaGroup,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply
        )
        service.sendMediaGroup(sendMediaGroupMultipartBody)
    }

    suspend fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        livePeriod: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendLocation(
            chatId,
            latitude,
            longitude,
            livePeriod,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun editMessageLiveLocation(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.editMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            latitude,
            longitude,
            replyMarkup
        )
    }

    suspend fun stopMessageLiveLocation(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.stopMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    suspend fun sendVenue(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String?,
        foursquareType: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

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
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendContact(
            chatId,
            phoneNumber,
            firstName,
            lastName,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun sendPoll(
        chatId: ChatId,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): TelegramBotResult<Message> = runApiOperation {
        service.sendPoll(
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
            allowSendingWithoutReply,
            replyMarkup,
        )
    }

    suspend fun sendChatAction(chatId: ChatId, action: ChatAction): CallResponse<Response<Boolean>> {
        return service.sendChatAction(chatId, action)
    }

    suspend fun getUserProfilePhotos(
        userId: Long,
        offset: Long?,
        limit: Int?
    ): CallResponse<Response<UserProfilePhotos>> {

        return service.getUserProfilePhotos(userId, offset, limit)
    }

    suspend fun getFile(fileId: String): CallResponse<Response<File>> {

        return service.getFile(fileId)
    }

    suspend fun downloadFile(filePath: String): CallResponse<ResponseBody> {
        return service.downloadFile("${apiUrl}file/bot$token/$filePath")
    }

    suspend fun banChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Long? = null
    ): CallResponse<Response<Boolean>> {
        return service.banChatMember(chatId, userId, untilDate)
    }

    suspend fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean?,
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.unbanChatMember(
            chatId,
            userId,
            onlyIfBanned,
        )
    }

    suspend fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Long? = null
    ): CallResponse<Response<Boolean>> {

        return service.restrictChatMember(
            chatId,
            userId,
            gson.toJson(chatPermissions),
            untilDate
        )
    }

    suspend fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        isAnonymous: Boolean?,
        canChangeInfo: Boolean?,
        canPostMessages: Boolean?,
        canEditMessages: Boolean?,
        canDeleteMessages: Boolean?,
        canInviteUsers: Boolean?,
        canRestrictMembers: Boolean?,
        canPinMessages: Boolean?,
        canPromoteMembers: Boolean?
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.promoteChatMember(
            chatId,
            userId,
            isAnonymous,
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

    suspend fun setChatPermissions(
        chatId: ChatId,
        permissions: ChatPermissions
    ): CallResponse<Response<Boolean>> {

        return service.setChatPermissions(chatId, gson.toJson(permissions))
    }

    suspend fun exportChatInviteLink(chatId: ChatId): CallResponse<Response<String>> {

        return service.exportChatInviteLink(chatId)
    }

    suspend fun setChatPhoto(
        chatId: ChatId,
        photo: SystemFile
    ): CallResponse<Response<Boolean>> {
        return service.setChatPhoto(chatId, photo.toMultipartBodyPart("photo"))
    }

    suspend fun deleteChatPhoto(chatId: ChatId): CallResponse<Response<Boolean>> {

        return service.deleteChatPhoto(chatId)
    }

    suspend fun setChatTitle(chatId: ChatId, title: String): CallResponse<Response<Boolean>> {

        return service.setChatTitle(chatId, title)
    }

    suspend fun setChatDescription(chatId: ChatId, description: String): CallResponse<Response<Boolean>> {

        return service.setChatDescription(chatId, description)
    }

    suspend fun pinChatMessage(
        chatId: ChatId,
        messageId: Long,
        disableNotification: Boolean?
    ): CallResponse<Response<Boolean>> {

        return service.pinChatMessage(chatId, messageId, disableNotification)
    }

    suspend fun unpinChatMessage(
        chatId: ChatId,
        messageId: Long?
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.unpinChatMessage(
            chatId,
            messageId
        )
    }

    suspend fun unpinAllChatMessages(
        chatId: ChatId
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.unpinAllChatMessages(
            chatId
        )
    }

    suspend fun leaveChat(chatId: ChatId): CallResponse<Response<Boolean>> {

        return service.leaveChat(chatId)
    }

    suspend fun getChat(chatId: ChatId): TelegramBotResult<Chat> =
        runApiOperation { service.getChat(chatId) }

    suspend fun getChatAdministrators(chatId: ChatId): TelegramBotResult<List<ChatMember>> =
        runApiOperation { service.getChatAdministrators(chatId) }

    suspend fun getChatMemberCount(chatId: ChatId): CallResponse<Response<Int>> {

        return service.getChatMemberCount(chatId)
    }

    suspend fun getChatMember(
        chatId: ChatId,
        userId: Long,
    ): TelegramBotResult<ChatMember> = runApiOperation {
        service.getChatMember(
            chatId,
            userId,
        )
    }

    suspend fun setChatStickerSet(
        chatId: ChatId,
        stickerSetName: String,
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.setChatStickerSet(
            chatId,
            stickerSetName,
        )
    }

    suspend fun deleteChatStickerSet(
        chatId: ChatId
    ): TelegramBotResult<Boolean> = runApiOperation { service.deleteChatStickerSet(chatId) }

    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String?,
        showAlert: Boolean?,
        url: String?,
        cacheTime: Int?
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.answerCallbackQuery(
            callbackQueryId,
            text,
            showAlert,
            url,
            cacheTime
        )
    }

    suspend fun logOut(): CallResponse<Response<Boolean>> {

        return service.logOut()
    }

    /**
     * Updating messages
     */

    suspend fun editMessageText(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        text: String,
        parseMode: ParseMode?,
        disableWebPagePreview: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

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

    suspend fun editMessageCaption(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        caption: String,
        parseMode: ParseMode?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.editMessageCaption(
            chatId,
            messageId,
            inlineMessageId,
            caption,
            parseMode,
            replyMarkup
        )
    }

    suspend fun editMessageMedia(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        media: InputMedia,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.editMessageMedia(
            chatId,
            messageId,
            inlineMessageId,
            media,
            replyMarkup
        )
    }

    suspend fun editMessageReplyMarkup(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.editMessageReplyMarkup(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    suspend fun stopPoll(
        chatId: ChatId,
        messageId: Long,
        replyMarkup: InlineKeyboardMarkup?,
    ): TelegramBotResult<Poll> = runApiOperation {
        service.stopPoll(
            chatId,
            messageId,
            replyMarkup,
        )
    }

    suspend fun deleteMessage(
        chatId: ChatId,
        messageId: Long,
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.deleteMessage(
            chatId,
            messageId,
        )
    }

    suspend fun sendInvoice(
        chatId: ChatId,
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
        allowSendingWithoutReply: Boolean?,
        replyMarkup: InlineKeyboardMarkup?
    ): TelegramBotResult<Message> = runApiOperation {
        service.sendInvoice(
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
            replyToMessageId = replyToMessageId,
            allowSendingWithoutReply = allowSendingWithoutReply
        )
    }

    suspend fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>?,
        errorMessage: String?
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.answerShippingQuery(
            shippingQueryId,
            ok,
            shippingOptions,
            errorMessage
        )
    }

    suspend fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String?
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.answerPreCheckoutQuery(
            preCheckoutQueryId,
            ok,
            errorMessage
        )
    }

    /***
     * Stickers
     */

    suspend fun sendSticker(
        chatId: ChatId,
        sticker: SystemFile,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendSticker(
            chatId,
            sticker.toMultipartBodyPart("photo"),
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    suspend fun sendSticker(
        chatId: ChatId,
        sticker: String,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): CallResponse<Response<Message>> {

        return service.sendSticker(
            chatId,
            sticker,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun getStickerSet(
        name: String
    ): CallResponse<Response<StickerSet>> {

        return service.getStickerSet(name)
    }

    suspend fun uploadStickerFile(
        userId: Long,
        pngSticker: SystemFile
    ): CallResponse<Response<File>> {

        return service.uploadStickerFile(
            convertString(userId.toString()),
            pngSticker.toMultipartBodyPart("photo")
        )
    }

    suspend fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: SystemFile,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>> {

        return service.createNewStickerSet(
            convertString(userId.toString()),
            convertString(name),
            convertString(title),
            pngSticker.toMultipartBodyPart("photo"),
            convertString(emojis),
            if (containsMasks != null) convertString(containsMasks.toString()) else null,
            if (maskPosition != null) convertJson(maskPosition.toString()) else null
        )
    }

    suspend fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: String,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>> {

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

    suspend fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: SystemFile,
        emojis: String,
        maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>> {

        return service.addStickerToSet(
            convertString(userId.toString()),
            convertString(name),
            pngSticker.toMultipartBodyPart("photo"),
            convertString(emojis),
            if (maskPosition != null) convertJson(maskPosition.toString()) else null
        )
    }

    suspend fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: String,
        emojis: String,
        maskPosition: MaskPosition?
    ): CallResponse<Response<Boolean>> {

        return service.addStickerToSet(
            userId,
            name,
            pngSticker,
            emojis,
            maskPosition
        )
    }

    suspend fun setStickerPositionInSet(
        sticker: String,
        position: Int
    ): CallResponse<Response<Boolean>> {

        return service.setStickerPositionInSet(
            sticker,
            position
        )
    }

    suspend fun deleteStickerFromSet(
        sticker: String
    ): CallResponse<Response<Boolean>> {

        return service.deleteStickerFromSet(
            sticker
        )
    }

    suspend fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int?,
        isPersonal: Boolean,
        nextOffset: String?,
        switchPmText: String?,
        switchPmParameter: String?
    ): TelegramBotResult<Boolean> = runApiOperation {
        val inlineQueryResultsType = object : TypeToken<List<InlineQueryResult>>() {}.type
        val serializedInlineQueryResults = gson.toJson(inlineQueryResults, inlineQueryResultsType)

        service.answerInlineQuery(
            inlineQueryId,
            serializedInlineQueryResults,
            cacheTime,
            isPersonal,
            nextOffset,
            switchPmText,
            switchPmParameter
        )
    }

    suspend fun getMyCommands(): TelegramBotResult<List<BotCommand>> =
        runApiOperation { service.getMyCommands() }

    suspend fun setMyCommands(
        commands: List<BotCommand>
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.setMyCommands(
            gson.toJson(commands)
        )
    }

    suspend fun sendDice(
        chatId: ChatId,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): TelegramBotResult<Message> = runApiOperation {
        service.sendDice(
            chatId,
            emoji,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    suspend fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String
    ): TelegramBotResult<Boolean> = runApiOperation {
        service.setChatAdministratorCustomTitle(
            chatId,
            userId,
            customTitle
        )
    }

    private suspend inline fun <T> runApiOperation(crossinline block: suspend () -> CallResponse<Response<T>>): TelegramBotResult<T> {
        val apiResponse = try {
            block()
        } catch (e: Exception) {
            return TelegramBotResult.Error.Unknown(e)
        }

        return apiResponseMapper.mapToTelegramBotResult(apiResponse)
    }

    /**
     * Transforms a list of strings into a string with the values separated by commas and
     * the result surrounded by square brackets. Some List<String> parameters of the
     * Telegram Bot Api operations require to be serialized in that format and retrofit doesn't
     * know how to properly do that (and can't be configured to do it for query params for example).
     *
     * e.g. for a list composed by test1, test2 and test3, the result must be ["test1","test2", "test3"].
     */
    private fun List<String>.serialize(): String = joinToString(
        separator = ",",
        prefix = "[",
        postfix = "]"
    ) { "\"$it\"" }
}
