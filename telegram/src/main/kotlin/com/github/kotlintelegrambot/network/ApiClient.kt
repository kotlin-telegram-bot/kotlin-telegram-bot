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
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit
import java.io.File as SystemFile

internal val PLAIN_TEXT_MIME = MediaType.parse("text/plain")
internal val APPLICATION_JSON_MIME = MediaType.parse("application/json")

private fun convertString(text: String) = RequestBody.create(PLAIN_TEXT_MIME, text)
private fun convertJson(text: String) = RequestBody.create(APPLICATION_JSON_MIME, text)

internal class ApiClient(
    private val token: String,
    private val apiUrl: String,
    private val botTimeout: Int = 30,
    logLevel: LogLevel,
    proxy: Proxy = Proxy.NO_PROXY,
    private val gson: Gson,
    private val multipartBodyFactory: MultipartBodyFactory = MultipartBodyFactory(GsonFactory.createForMultipartBodyFactory()),
    private val apiRequestSender: ApiRequestSender = ApiRequestSender(),
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

    fun getUpdates(
        offset: Long?,
        limit: Int?,
        timeout: Int?,
        allowedUpdates: List<String>?
    ): TelegramBotResult<List<Update>> = service.getUpdates(
        offset,
        limit,
        timeout,
        allowedUpdates?.serialize(),
    ).runApiOperation()

    fun setWebhook(
        url: String,
        certificate: TelegramFile? = null,
        ipAddress: String? = null,
        maxConnections: Int? = null,
        allowedUpdates: List<String>? = null,
        dropPendingUpdates: Boolean? = null
    ): Call<Response<Boolean>> = when (certificate) {
        is ByFileId -> service.setWebhookWithCertificateAsFileId(
            url = url,
            certificateFileId = certificate.fileId,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates
        )
        is ByUrl -> service.setWebhookWithCertificateAsFileUrl(
            url = url,
            certificateUrl = certificate.url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates
        )
        is ByFile -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.file.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                mediaType = MediaTypeConstants.UTF_8_TEXT
            ),
            ipAddress = ipAddress?.toMultipartBodyPart(ApiConstants.SetWebhook.IP_ADDRESS),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES),
            dropPendingUpdates = dropPendingUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.DROP_PENDING_UPDATES)
        )
        is ByByteArray -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.fileBytes.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                filename = certificate.filename,
                mediaType = MediaTypeConstants.UTF_8_TEXT
            ),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES),
            dropPendingUpdates = dropPendingUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.DROP_PENDING_UPDATES)
        )
        null -> service.setWebhook(
            url = url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates
        )
    }

    fun deleteWebhook(
        dropPendingUpdates: Boolean? = null
    ): Call<Response<Boolean>> = service.deleteWebhook(dropPendingUpdates)

    fun getWebhookInfo(): Call<Response<WebhookInfo>> = service.getWebhookInfo()

    fun getMe(): TelegramBotResult<User> {
        return service.getMe().runApiOperation()
    }

    fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode?,
        disableWebPagePreview: Boolean?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): TelegramBotResult<Message> = service.sendMessage(
        chatId,
        text,
        parseMode,
        disableWebPagePreview,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).runApiOperation()

    fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        disableNotification: Boolean?
    ): TelegramBotResult<Message> = service.forwardMessage(
        chatId,
        fromChatId,
        disableNotification,
        messageId,
    ).runApiOperation()

    fun copyMessage(
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
    ): Call<Response<MessageId>> {
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

    fun sendPhoto(
        chatId: ChatId,
        photo: TelegramFile,
        caption: String?,
        parseMode: ParseMode?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> = when (photo) {
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

    fun sendAudio(
        chatId: ChatId,
        audio: TelegramFile,
        duration: Int?,
        performer: String?,
        title: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> = when (audio) {
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

    fun sendDocument(
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
    ): Call<Response<Message>> = when (document) {
        is ByFile, is ByByteArray -> service.sendDocument(
            chatId,
            when (document) {
                is ByFile -> document.file.toMultipartBodyPart("document", mimeType)
                is ByByteArray -> document.fileBytes.toMultipartBodyPart("document", document.filename, mimeType)
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

    fun sendVideo(
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
    ): Call<Response<Message>> = when (video) {
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

    fun sendGame(
        chatId: ChatId,
        gameShortName: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): TelegramBotResult<Message> = service.sendGame(
        chatId,
        gameShortName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).runApiOperation()

    @Deprecated("Use overloaded version instead")
    fun sendAnimation(
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
    ): Call<Response<Message>> {

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

    fun sendAnimation(
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
    ): Call<Response<Message>> = when (animation) {
        is ByFile, is ByByteArray -> service.sendAnimation(
            chatId,
            when (animation) {
                is ByFile -> animation.file.toMultipartBodyPart("video")
                is ByByteArray -> animation.fileBytes.toMultipartBodyPart("video", animation.filename)
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

    fun sendVoice(
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
    ): Call<Response<Message>> = when (audio) {
        is ByFile, is ByByteArray -> service.sendVoice(
            chatId,
            when (audio) {
                is ByFile -> audio.file.toMultipartBodyPart("voice", AUDIO_OGG)
                is ByByteArray -> audio.fileBytes.toMultipartBodyPart("voice", audio.filename, AUDIO_OGG)
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

    fun sendVideoNote(
        chatId: ChatId,
        videoNote: ByFile,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

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

    fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: ByFileId,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

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

    fun sendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null
    ): TelegramBotResult<List<Message>> {
        val sendMediaGroupMultipartBody = multipartBodyFactory.createForSendMediaGroup(
            chatId,
            mediaGroup,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply
        )
        return service.sendMediaGroup(sendMediaGroupMultipartBody).runApiOperation()
    }

    fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        livePeriod: Int?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?,
        proximityAlertRadius: Int?
    ): Call<Response<Message>> {

        return service.sendLocation(
            chatId,
            latitude,
            longitude,
            livePeriod,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup,
            proximityAlertRadius
        )
    }

    fun editMessageLiveLocation(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup?,
        proximityAlertRadius: Int?
    ): Call<Response<Message>> {

        return service.editMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            latitude,
            longitude,
            replyMarkup,
            proximityAlertRadius
        )
    }

    fun stopMessageLiveLocation(
        chatId: ChatId?,
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
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String?,
        foursquareType: String?,
        googlePlaceId: String?,
        googlePlaceType: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
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
            googlePlaceId,
            googlePlaceType,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String?,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

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

    fun sendPoll(
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
    ): TelegramBotResult<Message> = service.sendPoll(
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
    ).runApiOperation()

    fun sendChatAction(chatId: ChatId, action: ChatAction): TelegramBotResult<Boolean> {
        return service.sendChatAction(chatId, action).runApiOperation()
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

    fun banChatMember(chatId: ChatId, userId: Long, untilDate: Long? = null): Call<Response<Boolean>> {

        return service.banChatMember(chatId, userId, untilDate)
    }

    fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean?,
    ): TelegramBotResult<Boolean> = service.unbanChatMember(
        chatId,
        userId,
        onlyIfBanned,
    ).runApiOperation()

    fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Long? = null
    ): Call<Response<Boolean>> {

        return service.restrictChatMember(
            chatId,
            userId,
            gson.toJson(chatPermissions),
            untilDate
        )
    }

    fun promoteChatMember(
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
    ): TelegramBotResult<Boolean> = service.promoteChatMember(
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
    ).runApiOperation()

    fun setChatPermissions(chatId: ChatId, permissions: ChatPermissions): Call<Response<Boolean>> {

        return service.setChatPermissions(chatId, gson.toJson(permissions))
    }

    fun exportChatInviteLink(chatId: ChatId): Call<Response<String>> {

        return service.exportChatInviteLink(chatId)
    }

    fun setChatPhoto(
        chatId: ChatId,
        photo: SystemFile
    ): Call<Response<Boolean>> {
        return service.setChatPhoto(chatId, photo.toMultipartBodyPart("photo"))
    }

    fun deleteChatPhoto(chatId: ChatId): Call<Response<Boolean>> {

        return service.deleteChatPhoto(chatId)
    }

    fun setChatTitle(chatId: ChatId, title: String): Call<Response<Boolean>> {

        return service.setChatTitle(chatId, title)
    }

    fun setChatDescription(chatId: ChatId, description: String): Call<Response<Boolean>> {

        return service.setChatDescription(chatId, description)
    }

    fun pinChatMessage(
        chatId: ChatId,
        messageId: Long,
        disableNotification: Boolean?
    ): TelegramBotResult<Boolean> {
        return service.pinChatMessage(
            chatId,
            messageId,
            disableNotification,
        ).runApiOperation()
    }

    fun unpinChatMessage(
        chatId: ChatId,
        messageId: Long?
    ): TelegramBotResult<Boolean> = service.unpinChatMessage(
        chatId,
        messageId
    ).runApiOperation()

    fun unpinAllChatMessages(
        chatId: ChatId
    ): TelegramBotResult<Boolean> = service.unpinAllChatMessages(
        chatId
    ).runApiOperation()

    fun leaveChat(chatId: ChatId): TelegramBotResult<Boolean> {
        return service.leaveChat(chatId).runApiOperation()
    }

    fun getChat(chatId: ChatId): TelegramBotResult<Chat> = service.getChat(chatId).runApiOperation()

    fun getChatAdministrators(chatId: ChatId): TelegramBotResult<List<ChatMember>> =
        service.getChatAdministrators(chatId).runApiOperation()

    fun getChatMemberCount(chatId: ChatId): Call<Response<Int>> {

        return service.getChatMemberCount(chatId)
    }

    fun getChatMember(
        chatId: ChatId,
        userId: Long,
    ): TelegramBotResult<ChatMember> = service.getChatMember(
        chatId,
        userId,
    ).runApiOperation()

    fun setChatStickerSet(
        chatId: ChatId,
        stickerSetName: String,
    ): TelegramBotResult<Boolean> = service.setChatStickerSet(
        chatId,
        stickerSetName,
    ).runApiOperation()

    fun deleteChatStickerSet(
        chatId: ChatId
    ): TelegramBotResult<Boolean> = service.deleteChatStickerSet(chatId).runApiOperation()

    fun answerCallbackQuery(
        callbackQueryId: String,
        text: String?,
        showAlert: Boolean?,
        url: String?,
        cacheTime: Int?
    ): TelegramBotResult<Boolean> = service.answerCallbackQuery(
        callbackQueryId,
        text,
        showAlert,
        url,
        cacheTime
    ).runApiOperation()

    fun logOut(): Call<Response<Boolean>> {

        return service.logOut()
    }

    fun close(): Call<Response<Boolean>> {

        return service.close()
    }

    /**
     * Updating messages
     */

    fun editMessageText(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        text: String,
        parseMode: ParseMode?,
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
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        caption: String,
        parseMode: ParseMode?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.editMessageCaption(
            chatId,
            messageId,
            inlineMessageId,
            caption,
            parseMode,
            replyMarkup
        )
    }

    fun editMessageMedia(
        chatId: ChatId?,
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
        chatId: ChatId?,
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
        chatId: ChatId,
        messageId: Long,
        replyMarkup: InlineKeyboardMarkup?,
    ): TelegramBotResult<Poll> = service.stopPoll(
        chatId,
        messageId,
        replyMarkup,
    ).runApiOperation()

    fun deleteMessage(
        chatId: ChatId,
        messageId: Long,
    ): TelegramBotResult<Boolean> = service.deleteMessage(
        chatId,
        messageId,
    ).runApiOperation()

    fun sendInvoice(
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
    ): TelegramBotResult<Message> = service.sendInvoice(
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
    ).runApiOperation()

    fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>?,
        errorMessage: String?
    ): TelegramBotResult<Boolean> = service.answerShippingQuery(
        shippingQueryId,
        ok,
        shippingOptions,
        errorMessage
    ).runApiOperation()

    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String?
    ): TelegramBotResult<Boolean> = service.answerPreCheckoutQuery(
        preCheckoutQueryId,
        ok,
        errorMessage
    ).runApiOperation()

    /***
     * Stickers
     */

    fun sendSticker(
        chatId: ChatId,
        sticker: SystemFile,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendSticker(
            chatId,
            sticker.toMultipartBodyPart("photo"),
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null,
            if (allowSendingWithoutReply != null) convertString(allowSendingWithoutReply.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null
        )
    }

    fun sendSticker(
        chatId: ChatId,
        sticker: String,
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendSticker(
            chatId,
            sticker,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
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
            pngSticker.toMultipartBodyPart("photo")
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
            pngSticker.toMultipartBodyPart("photo"),
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
            pngSticker.toMultipartBodyPart("photo"),
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
    ): TelegramBotResult<Boolean> {
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
        ).runApiOperation()
    }

    fun getMyCommands(): TelegramBotResult<List<BotCommand>> = service.getMyCommands().runApiOperation()

    fun setMyCommands(
        commands: List<BotCommand>
    ): TelegramBotResult<Boolean> = service.setMyCommands(
        gson.toJson(commands)
    ).runApiOperation()

    fun sendDice(
        chatId: ChatId,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): TelegramBotResult<Message> = service.sendDice(
        chatId,
        emoji,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).runApiOperation()

    fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String
    ): TelegramBotResult<Boolean> = service.setChatAdministratorCustomTitle(
        chatId,
        userId,
        customTitle
    ).runApiOperation()

    private fun <T> Call<Response<T>>.runApiOperation(): TelegramBotResult<T> {
        val apiResponse = try {
            apiRequestSender.send(this)
        } catch (exception: Exception) {
            return TelegramBotResult.Error.Unknown(exception)
        }

        return apiResponseMapper.mapToTelegramBotResult(apiResponse)
    }

    /**
     * Transforms a list of strings into a string with the values separated by commas and
     * the result surrounded by square brackets. Some of the List<String> parameters of the
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
