package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.LinkPreviewOptions
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.ReplyParameters
import com.github.kotlintelegrambot.entities.SentWebAppMessage
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByByteArray
import com.github.kotlintelegrambot.entities.TelegramFile.ByFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByFileId
import com.github.kotlintelegrambot.entities.TelegramFile.ByInputStream
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
import com.github.kotlintelegrambot.entities.reaction.ReactionType
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
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

internal val PLAIN_TEXT_MIME = "text/plain".toMediaTypeOrNull()
internal val APPLICATION_JSON_MIME = "application/json".toMediaTypeOrNull()

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
    private val apiResponseMapper: ApiResponseMapper = ApiResponseMapper(),
    private val httpClientInterceptors: List<Interceptor> = emptyList(),
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
            .also { builder -> httpClientInterceptors.forEach { builder.addInterceptor(it) } }
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
        allowedUpdates: List<String>?,
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
        dropPendingUpdates: Boolean? = null,
        secretToken: String? = null,
    ): Call<Response<Boolean>> = when (certificate) {
        is ByFileId -> service.setWebhookWithCertificateAsFileId(
            url = url,
            certificateFileId = certificate.fileId,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates,
            secretToken = secretToken,
        )

        is ByUrl -> service.setWebhookWithCertificateAsFileUrl(
            url = url,
            certificateUrl = certificate.url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates,
            secretToken = secretToken,
        )

        is ByFile -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.file.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                mediaType = MediaTypeConstants.UTF_8_TEXT,
            ),
            ipAddress = ipAddress?.toMultipartBodyPart(ApiConstants.SetWebhook.IP_ADDRESS),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES),
            dropPendingUpdates = dropPendingUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.DROP_PENDING_UPDATES),
            secretToken = secretToken?.toMultipartBodyPart(ApiConstants.SetWebhook.SECRET_TOKEN),
        )

        is ByByteArray -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate.fileBytes.toMultipartBodyPart(
                partName = ApiConstants.SetWebhook.CERTIFICATE,
                filename = certificate.filename,
                mediaType = MediaTypeConstants.UTF_8_TEXT,
            ),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES),
            dropPendingUpdates = dropPendingUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.DROP_PENDING_UPDATES),
            secretToken = secretToken?.toMultipartBodyPart(ApiConstants.SetWebhook.SECRET_TOKEN),
        )

        is ByInputStream -> service.setWebhookWithCertificateAsFile(
            url = url.toMultipartBodyPart(ApiConstants.SetWebhook.URL),
            certificate = certificate
                .let { if (it.contentType != null) it else it.copy(contentType = MediaTypeConstants.UTF_8_TEXT.toMediaType()) }
                .asMultipartBodyPart(ApiConstants.SetWebhook.CERTIFICATE),
            ipAddress = ipAddress?.toMultipartBodyPart(ApiConstants.SetWebhook.IP_ADDRESS),
            maxConnections = maxConnections?.toMultipartBodyPart(ApiConstants.SetWebhook.MAX_CONNECTIONS),
            allowedUpdates = allowedUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.ALLOWED_UPDATES),
            dropPendingUpdates = dropPendingUpdates?.toMultipartBodyPart(ApiConstants.SetWebhook.DROP_PENDING_UPDATES),
            secretToken = secretToken?.toMultipartBodyPart(ApiConstants.SetWebhook.SECRET_TOKEN),
        )

        null -> service.setWebhook(
            url = url,
            ipAddress = ipAddress,
            maxConnections = maxConnections,
            allowedUpdates = allowedUpdates,
            dropPendingUpdates = dropPendingUpdates,
            secretToken = secretToken,
        )
    }

    fun deleteWebhook(
        dropPendingUpdates: Boolean? = null,
    ): Call<Response<Boolean>> = service.deleteWebhook(dropPendingUpdates)

    fun getWebhookInfo(): Call<Response<WebhookInfo>> = service.getWebhookInfo()

    fun getMe(): TelegramBotResult<User> {
        return service.getMe().runApiOperation()
    }

    fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        messageThreadId: Long? = null,
        entities: List<MessageEntity>? = null,
        linkPreviewOptions: LinkPreviewOptions? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): TelegramBotResult<Message> = service.sendMessage(
        chatId,
        text,
        parseMode,
        disableNotification,
        protectContent,
        replyMarkup,
        messageThreadId,
        if (entities != null) gson.toJson(entities) else null,
        if (linkPreviewOptions != null) gson.toJson(linkPreviewOptions) else null,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId,
        messageEffectId,
        allowPaidBroadcast,
    ).runApiOperation()

    fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        disableNotification: Boolean?,
        protectContent: Boolean?,
    ): TelegramBotResult<Message> = service.forwardMessage(
        chatId,
        fromChatId,
        disableNotification,
        protectContent,
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
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        replyParameters: com.github.kotlintelegrambot.entities.ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<MessageId>> {
        return service.copyMessage(
            chatId,
            fromChatId,
            messageId,
            caption,
            parseMode,
            if (captionEntities != null) gson.toJson(captionEntities) else null,
            disableNotification,
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendPhoto(
        chatId: ChatId,
        photo: TelegramFile,
        caption: String?,
        parseMode: ParseMode?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        messageThreadId: Long?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (photo) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendPhoto(
            chatId,
            when (photo) {
                is ByFile -> photo.file.toMultipartBodyPart("photo")
                is ByByteArray -> photo.fileBytes.toMultipartBodyPart("photo", photo.filename)
                is ByInputStream -> photo.asMultipartBodyPart("photo")
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (messageThreadId != null) convertJson(messageThreadId.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendAudio(
        chatId: ChatId,
        audio: TelegramFile,
        duration: Int?,
        performer: String?,
        title: String?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (audio) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendAudio(
            chatId,
            when (audio) {
                is ByFile -> audio.file.toMultipartBodyPart("audio")
                is ByByteArray -> audio.fileBytes.toMultipartBodyPart("audio", audio.filename)
                is ByInputStream -> audio.asMultipartBodyPart("audio")
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (performer != null) convertString(performer) else null,
            if (title != null) convertString(title) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendDocument(
        chatId: ChatId,
        document: TelegramFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        mimeType: String? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (document) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendDocument(
            chatId,
            when (document) {
                is ByFile -> document.file.toMultipartBodyPart("document", mimeType)
                is ByByteArray -> document.fileBytes.toMultipartBodyPart("document", document.filename, mimeType)
                is ByInputStream -> document.asMultipartBodyPart("document")
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableContentTypeDetection != null) convertString(disableContentTypeDetection.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendVideo(
        chatId: ChatId,
        video: TelegramFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String?,
        parseMode: ParseMode?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (video) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendVideo(
            chatId,
            when (video) {
                is ByFile -> video.file.toMultipartBodyPart("video")
                is ByByteArray -> video.fileBytes.toMultipartBodyPart("video", video.filename)
                is ByInputStream -> video.asMultipartBodyPart("video")
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            parseMode,
            disableNotification,
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendGame(
        chatId: ChatId,
        gameShortName: String,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): TelegramBotResult<Message> = service.sendGame(
        chatId,
        gameShortName,
        disableNotification,
        protectContent,
        replyMarkup,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId,
        messageEffectId,
        allowPaidBroadcast,
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
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
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
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
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
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (animation) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendAnimation(
            chatId,
            when (animation) {
                is ByFile -> animation.file.toMultipartBodyPart("video")
                is ByByteArray -> animation.fileBytes.toMultipartBodyPart("video", animation.filename)
                is ByInputStream -> animation.asMultipartBodyPart("video")
                else -> throw NotImplementedError() // KT-31622
            },
            if (duration != null) convertString(duration.toString()) else null,
            if (width != null) convertString(width.toString()) else null,
            if (height != null) convertString(height.toString()) else null,
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
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
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> = when (audio) {
        is ByFile, is ByByteArray, is ByInputStream -> service.sendVoice(
            chatId,
            when (audio) {
                is ByFile -> audio.file.toMultipartBodyPart("voice", AUDIO_OGG)
                is ByByteArray -> audio.fileBytes.toMultipartBodyPart("voice", audio.filename, AUDIO_OGG)
                is ByInputStream -> audio.asMultipartBodyPart("voice")
                else -> throw NotImplementedError() // KT-31622
            },
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode.modeName) else null,
            if (captionEntities != null) convertJson(gson.toJson(captionEntities)) else null,
            if (duration != null) convertString(duration.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
            if (showCaptionAboveMedia != null) convertString(showCaptionAboveMedia.toString()) else null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
            showCaptionAboveMedia,
        )
    }

    fun sendVideoNote(
        chatId: ChatId,
        videoNote: ByFile,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendVideoNote(
            chatId,
            videoNote.file.toMultipartBodyPart("video_note"),
            if (duration != null) convertString(duration.toString()) else null,
            if (length != null) convertString(length.toString()) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
        )
    }

    fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: ByFileId,
        duration: Int?,
        length: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendVideoNote(
            chatId,
            videoNoteId.fileId,
            duration,
            length,
            disableNotification,
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
        )
    }

    fun sendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): TelegramBotResult<List<Message>> {
        val sendMediaGroupMultipartBody = multipartBodyFactory.createForSendMediaGroup(
            chatId,
            mediaGroup,
            disableNotification,
            protectContent,
        )
        val extraParts = listOfNotNull(
            replyParameters?.let { gson.toJson(it).toMultipartBodyPart("reply_parameters") },
            businessConnectionId?.toMultipartBodyPart("business_connection_id"),
            messageEffectId?.toMultipartBodyPart("message_effect_id"),
            allowPaidBroadcast?.toMultipartBodyPart("allow_paid_broadcast"),
        )
        return service.sendMediaGroup(sendMediaGroupMultipartBody + extraParts).runApiOperation()
    }

    fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        livePeriod: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        proximityAlertRadius: Int?,
        horizontalAccuracy: Float?,
        heading: Int?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendLocation(
            chatId,
            latitude,
            longitude,
            livePeriod,
            disableNotification,
            protectContent,
            replyMarkup,
            proximityAlertRadius,
            horizontalAccuracy,
            heading,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
        )
    }

    fun editMessageLiveLocation(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup?,
        proximityAlertRadius: Int?,
        horizontalAccuracy: Float?,
        heading: Int?,
        businessConnectionId: String? = null,
    ): Call<Response<Message>> {
        return service.editMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            latitude,
            longitude,
            replyMarkup,
            proximityAlertRadius,
            horizontalAccuracy,
            heading,
            businessConnectionId,
        )
    }

    fun stopMessageLiveLocation(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?,
        businessConnectionId: String? = null,
    ): Call<Response<Message>> {
        return service.stopMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup,
            businessConnectionId,
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
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
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
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
        )
    }

    fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendContact(
            chatId,
            phoneNumber,
            firstName,
            lastName,
            disableNotification,
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
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
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
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
        protectContent,
        replyMarkup,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId,
        messageEffectId,
        allowPaidBroadcast,
    ).runApiOperation()

    fun sendChatAction(
        chatId: ChatId,
        action: ChatAction,
        messageThreadId: Long? = null,
        businessConnectionId: String? = null,
    ): TelegramBotResult<Boolean> {
        return service.sendChatAction(chatId, action, messageThreadId, businessConnectionId).runApiOperation()
    }

    fun getUserProfilePhotos(
        userId: Long,
        offset: Long?,
        limit: Int?,
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

    fun approveChatJoinRequest(chatId: ChatId, userId: Long): Call<Response<Boolean>> {
        return service.approveChatJoinRequest(chatId, userId)
    }

    fun declineChatJoinRequest(chatId: ChatId, userId: Long): Call<Response<Boolean>> {
        return service.declineChatJoinRequest(chatId, userId)
    }

    fun createChatInviteLink(
        chatId: ChatId,
        name: String?,
        expireDate: Int?,
        memberLimit: Int?,
        createsJoinRequest: Boolean?,
    ): Call<Response<Boolean>> {
        return service.createChatInviteLink(
            chatId,
            name,
            expireDate,
            memberLimit,
            createsJoinRequest,
        )
    }

    fun editChatInviteLink(
        chatId: ChatId,
        inviteLink: String,
        name: String? = null,
        expireDate: Int? = null,
        memberLimit: Int? = null,
        createsJoinRequest: Boolean? = null,
    ): Call<Response<Boolean>> {
        return service.editChatInviteLink(
            chatId,
            inviteLink,
            name,
            expireDate,
            memberLimit,
            createsJoinRequest,
        )
    }

    fun revokeChatInviteLink(
        chatId: ChatId,
        inviteLink: String,
    ): Call<Response<Boolean>> {
        return service.revokeChatInviteLink(
            chatId,
            inviteLink,
        )
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
        untilDate: Long? = null,
    ): Call<Response<Boolean>> {
        return service.restrictChatMember(
            chatId,
            userId,
            gson.toJson(chatPermissions),
            untilDate,
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
        canPromoteMembers: Boolean?,
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
        canPromoteMembers,
    ).runApiOperation()

    fun setChatPermissions(chatId: ChatId, permissions: ChatPermissions): Call<Response<Boolean>> {
        return service.setChatPermissions(chatId, gson.toJson(permissions))
    }

    fun exportChatInviteLink(chatId: ChatId): Call<Response<String>> {
        return service.exportChatInviteLink(chatId)
    }

    fun setChatPhoto(
        chatId: ChatId,
        photo: SystemFile,
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
        disableNotification: Boolean?,
    ): TelegramBotResult<Boolean> {
        return service.pinChatMessage(
            chatId,
            messageId,
            disableNotification,
        ).runApiOperation()
    }

    fun unpinChatMessage(
        chatId: ChatId,
        messageId: Long?,
    ): TelegramBotResult<Boolean> = service.unpinChatMessage(
        chatId,
        messageId,
    ).runApiOperation()

    fun unpinAllChatMessages(
        chatId: ChatId,
    ): TelegramBotResult<Boolean> = service.unpinAllChatMessages(
        chatId,
    ).runApiOperation()

    // --- Forum topics (Bot API 6.3 / 6.4) ---

    fun createForumTopic(
        chatId: ChatId,
        name: String,
        iconColor: Int? = null,
        iconCustomEmojiId: String? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.ForumTopic> =
        service.createForumTopic(chatId, name, iconColor, iconCustomEmojiId).runApiOperation()

    fun editForumTopic(
        chatId: ChatId,
        messageThreadId: Long,
        name: String? = null,
        iconCustomEmojiId: String? = null,
    ): TelegramBotResult<Boolean> =
        service.editForumTopic(chatId, messageThreadId, name, iconCustomEmojiId).runApiOperation()

    fun closeForumTopic(chatId: ChatId, messageThreadId: Long): TelegramBotResult<Boolean> =
        service.closeForumTopic(chatId, messageThreadId).runApiOperation()

    fun reopenForumTopic(chatId: ChatId, messageThreadId: Long): TelegramBotResult<Boolean> =
        service.reopenForumTopic(chatId, messageThreadId).runApiOperation()

    fun deleteForumTopic(chatId: ChatId, messageThreadId: Long): TelegramBotResult<Boolean> =
        service.deleteForumTopic(chatId, messageThreadId).runApiOperation()

    fun unpinAllForumTopicMessages(chatId: ChatId, messageThreadId: Long): TelegramBotResult<Boolean> =
        service.unpinAllForumTopicMessages(chatId, messageThreadId).runApiOperation()

    fun getForumTopicIconStickers(): TelegramBotResult<List<com.github.kotlintelegrambot.entities.stickers.Sticker>> =
        service.getForumTopicIconStickers().runApiOperation()

    fun editGeneralForumTopic(chatId: ChatId, name: String): TelegramBotResult<Boolean> =
        service.editGeneralForumTopic(chatId, name).runApiOperation()

    fun closeGeneralForumTopic(chatId: ChatId): TelegramBotResult<Boolean> =
        service.closeGeneralForumTopic(chatId).runApiOperation()

    fun reopenGeneralForumTopic(chatId: ChatId): TelegramBotResult<Boolean> =
        service.reopenGeneralForumTopic(chatId).runApiOperation()

    fun hideGeneralForumTopic(chatId: ChatId): TelegramBotResult<Boolean> =
        service.hideGeneralForumTopic(chatId).runApiOperation()

    fun unhideGeneralForumTopic(chatId: ChatId): TelegramBotResult<Boolean> =
        service.unhideGeneralForumTopic(chatId).runApiOperation()

    // --- Batch forward/copy/delete (Bot API 7.0) ---

    fun forwardMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Long>,
        messageThreadId: Long? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
    ): TelegramBotResult<List<com.github.kotlintelegrambot.entities.MessageId>> =
        service.forwardMessages(
            chatId,
            fromChatId,
            gson.toJson(messageIds),
            messageThreadId,
            disableNotification,
            protectContent,
        ).runApiOperation()

    fun copyMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Long>,
        messageThreadId: Long? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        removeCaption: Boolean? = null,
    ): TelegramBotResult<List<com.github.kotlintelegrambot.entities.MessageId>> =
        service.copyMessages(
            chatId,
            fromChatId,
            gson.toJson(messageIds),
            messageThreadId,
            disableNotification,
            protectContent,
            removeCaption,
        ).runApiOperation()

    fun deleteMessages(
        chatId: ChatId,
        messageIds: List<Long>,
    ): TelegramBotResult<Boolean> = service.deleteMessages(
        chatId,
        gson.toJson(messageIds),
    ).runApiOperation()

    fun getUserChatBoosts(
        chatId: ChatId,
        userId: Long,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.UserChatBoosts> =
        service.getUserChatBoosts(chatId, userId).runApiOperation()

    // --- Bot info methods (Bot API 6.6 - 6.7) ---

    fun setMyDescription(description: String? = null, languageCode: String? = null): TelegramBotResult<Boolean> =
        service.setMyDescription(description, languageCode).runApiOperation()

    fun getMyDescription(languageCode: String? = null): TelegramBotResult<com.github.kotlintelegrambot.entities.BotDescription> =
        service.getMyDescription(languageCode).runApiOperation()

    fun setMyShortDescription(shortDescription: String? = null, languageCode: String? = null): TelegramBotResult<Boolean> =
        service.setMyShortDescription(shortDescription, languageCode).runApiOperation()

    fun getMyShortDescription(languageCode: String? = null): TelegramBotResult<com.github.kotlintelegrambot.entities.BotShortDescription> =
        service.getMyShortDescription(languageCode).runApiOperation()

    fun setMyName(name: String? = null, languageCode: String? = null): TelegramBotResult<Boolean> =
        service.setMyName(name, languageCode).runApiOperation()

    fun getMyName(languageCode: String? = null): TelegramBotResult<com.github.kotlintelegrambot.entities.BotName> =
        service.getMyName(languageCode).runApiOperation()

    fun setChatMenuButton(
        chatId: ChatId? = null,
        menuButton: com.github.kotlintelegrambot.entities.MenuButton? = null,
    ): TelegramBotResult<Boolean> = service.setChatMenuButton(
        chatId,
        if (menuButton != null) gson.toJson(menuButton) else null,
    ).runApiOperation()

    fun getChatMenuButton(chatId: ChatId? = null): TelegramBotResult<com.github.kotlintelegrambot.entities.MenuButton> =
        service.getChatMenuButton(chatId).runApiOperation()

    fun setMyDefaultAdministratorRights(
        rights: com.github.kotlintelegrambot.entities.ChatAdministratorRights? = null,
        forChannels: Boolean? = null,
    ): TelegramBotResult<Boolean> = service.setMyDefaultAdministratorRights(
        if (rights != null) gson.toJson(rights) else null,
        forChannels,
    ).runApiOperation()

    fun getMyDefaultAdministratorRights(forChannels: Boolean? = null):
        TelegramBotResult<com.github.kotlintelegrambot.entities.ChatAdministratorRights> =
        service.getMyDefaultAdministratorRights(forChannels).runApiOperation()

    fun leaveChat(chatId: ChatId): TelegramBotResult<Boolean> {
        return service.leaveChat(chatId).runApiOperation()
    }

    fun getChat(chatId: ChatId): TelegramBotResult<com.github.kotlintelegrambot.entities.ChatFullInfo> =
        service.getChat(chatId).runApiOperation()

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
        chatId: ChatId,
    ): TelegramBotResult<Boolean> = service.deleteChatStickerSet(chatId).runApiOperation()

    fun answerCallbackQuery(
        callbackQueryId: String,
        text: String?,
        showAlert: Boolean?,
        url: String?,
        cacheTime: Int?,
    ): TelegramBotResult<Boolean> = service.answerCallbackQuery(
        callbackQueryId,
        text,
        showAlert,
        url,
        cacheTime,
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
        replyMarkup: ReplyMarkup?,
        entities: List<MessageEntity>? = null,
        businessConnectionId: String? = null,
    ): Call<Response<Message>> {
        return service.editMessageText(
            chatId,
            messageId,
            inlineMessageId,
            text,
            parseMode,
            replyMarkup,
            if (entities != null) gson.toJson(entities) else null,
            businessConnectionId,
        )
    }

    fun editMessageCaption(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        caption: String,
        parseMode: ParseMode?,
        replyMarkup: ReplyMarkup?,
        captionEntities: List<MessageEntity>? = null,
        businessConnectionId: String? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> {
        return service.editMessageCaption(
            chatId,
            messageId,
            inlineMessageId,
            caption,
            parseMode,
            replyMarkup,
            if (captionEntities != null) gson.toJson(captionEntities) else null,
            businessConnectionId,
            showCaptionAboveMedia,
        )
    }

    fun editMessageMedia(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        media: InputMedia,
        replyMarkup: ReplyMarkup?,
        businessConnectionId: String? = null,
        showCaptionAboveMedia: Boolean? = null,
    ): Call<Response<Message>> {
        return service.editMessageMedia(
            chatId,
            messageId,
            inlineMessageId,
            media,
            replyMarkup,
            businessConnectionId,
            showCaptionAboveMedia,
        )
    }

    fun editMessageReplyMarkup(
        chatId: ChatId?,
        messageId: Long?,
        inlineMessageId: String?,
        replyMarkup: ReplyMarkup?,
        businessConnectionId: String? = null,
    ): Call<Response<Message>> {
        return service.editMessageReplyMarkup(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup,
            businessConnectionId,
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
        recurring: Boolean?,
        maxTipAmount: Long?,
        suggestedTipAmounts: List<Long>?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: InlineKeyboardMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
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
        recurring = recurring,
        maxTipAmount = maxTipAmount,
        suggestedTipAmounts = suggestedTipAmounts,
        disableNotification = disableNotification,
        protectContent = protectContent,
        replyMarkup = replyMarkup,
        replyParameters = if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId = businessConnectionId,
        messageEffectId = messageEffectId,
        allowPaidBroadcast = allowPaidBroadcast,
    ).runApiOperation()

    fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>?,
        errorMessage: String?,
    ): TelegramBotResult<Boolean> = service.answerShippingQuery(
        shippingQueryId,
        ok,
        shippingOptions,
        errorMessage,
    ).runApiOperation()

    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String?,
    ): TelegramBotResult<Boolean> = service.answerPreCheckoutQuery(
        preCheckoutQueryId,
        ok,
        errorMessage,
    ).runApiOperation()

    fun refundStarPayment(
        userId: Long,
        telegramPaymentChargeId: String,
    ): TelegramBotResult<Boolean> = service.refundStarPayment(
        userId,
        telegramPaymentChargeId,
    ).runApiOperation()

    /***
     * Stickers
     */

    fun sendSticker(
        chatId: ChatId,
        sticker: SystemFile,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendSticker(
            chatId,
            sticker.toMultipartBodyPart("photo"),
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (protectContent != null) convertString(protectContent.toString()) else null,
            if (replyMarkup != null) convertJson(replyMarkup.toString()) else null,
            if (replyParameters != null) convertJson(gson.toJson(replyParameters)) else null,
            if (businessConnectionId != null) convertString(businessConnectionId) else null,
            if (messageEffectId != null) convertString(messageEffectId) else null,
            if (allowPaidBroadcast != null) convertString(allowPaidBroadcast.toString()) else null,
        )
    }

    fun sendSticker(
        chatId: ChatId,
        sticker: String,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyMarkup: ReplyMarkup?,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): Call<Response<Message>> {
        return service.sendSticker(
            chatId,
            sticker,
            disableNotification,
            protectContent,
            replyMarkup,
            if (replyParameters != null) gson.toJson(replyParameters) else null,
            businessConnectionId,
            messageEffectId,
            allowPaidBroadcast,
        )
    }

    fun getStickerSet(
        name: String,
    ): Call<Response<StickerSet>> {
        return service.getStickerSet(name)
    }

    fun uploadStickerFile(
        userId: Long,
        pngSticker: SystemFile,
    ): Call<Response<File>> {
        return service.uploadStickerFile(
            convertString(userId.toString()),
            pngSticker.toMultipartBodyPart("photo"),
        )
    }

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: SystemFile,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?,
    ): Call<Response<Boolean>> {
        return service.createNewStickerSet(
            convertString(userId.toString()),
            convertString(name),
            convertString(title),
            pngSticker.toMultipartBodyPart("photo"),
            convertString(emojis),
            if (containsMasks != null) convertString(containsMasks.toString()) else null,
            if (maskPosition != null) convertJson(maskPosition.toString()) else null,
        )
    }

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: String,
        emojis: String,
        containsMasks: Boolean?,
        maskPosition: MaskPosition?,
    ): Call<Response<Boolean>> {
        return service.createNewStickerSet(
            userId,
            name,
            title,
            pngSticker,
            emojis,
            containsMasks,
            maskPosition,
        )
    }

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: SystemFile,
        emojis: String,
        maskPosition: MaskPosition?,
    ): Call<Response<Boolean>> {
        return service.addStickerToSet(
            convertString(userId.toString()),
            convertString(name),
            pngSticker.toMultipartBodyPart("photo"),
            convertString(emojis),
            if (maskPosition != null) convertJson(maskPosition.toString()) else null,
        )
    }

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: String,
        emojis: String,
        maskPosition: MaskPosition?,
    ): Call<Response<Boolean>> {
        return service.addStickerToSet(
            userId,
            name,
            pngSticker,
            emojis,
            maskPosition,
        )
    }

    fun setStickerPositionInSet(
        sticker: String,
        position: Int,
    ): Call<Response<Boolean>> {
        return service.setStickerPositionInSet(
            sticker,
            position,
        )
    }

    fun deleteStickerFromSet(
        sticker: String,
    ): Call<Response<Boolean>> {
        return service.deleteStickerFromSet(
            sticker,
        )
    }

    fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int?,
        isPersonal: Boolean,
        nextOffset: String?,
        switchPmText: String?,
        switchPmParameter: String?,
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
            switchPmParameter,
        ).runApiOperation()
    }

    fun answerWebAppQuery(
        webAppQueryId: String,
        inlineQueryResult: InlineQueryResult,
    ): TelegramBotResult<SentWebAppMessage> {
        val inlineQueryResultsType = object : TypeToken<InlineQueryResult>() {}.type
        val serializedInlineQueryResults = gson.toJson(inlineQueryResult, inlineQueryResultsType)

        return service.answerWebAppQuery(
            webAppQueryId,
            serializedInlineQueryResults,
        ).runApiOperation()
    }

    fun getMyCommands(): TelegramBotResult<List<BotCommand>> = service.getMyCommands().runApiOperation()

    fun setMyCommands(
        commands: List<BotCommand>,
    ): TelegramBotResult<Boolean> = service.setMyCommands(
        gson.toJson(commands),
    ).runApiOperation()

    fun sendDice(
        chatId: ChatId,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        replyParameters: ReplyParameters? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): TelegramBotResult<Message> = service.sendDice(
        chatId,
        emoji,
        disableNotification,
        protectContent,
        replyMarkup,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId,
        messageEffectId,
        allowPaidBroadcast,
    ).runApiOperation()

    fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String,
    ): TelegramBotResult<Boolean> = service.setChatAdministratorCustomTitle(
        chatId,
        userId,
        customTitle,
    ).runApiOperation()

    fun setMessageReaction(
        chatId: ChatId,
        messageId: Long,
        reaction: List<ReactionType>,
        isBig: Boolean,
    ): TelegramBotResult<Boolean> {
        return service.setMessageReaction(
            chatId = chatId,
            messageId = messageId,
            reaction = gson.toJson(reaction),
            isBig = isBig,
        ).runApiOperation()
    }

    // --- Bot API 10.0 reaction deletion ---

    fun deleteMessageReaction(
        chatId: ChatId,
        messageId: Long,
        userId: Long? = null,
    ): TelegramBotResult<Boolean> = service.deleteMessageReaction(chatId, messageId, userId).runApiOperation()

    fun deleteAllMessageReactions(
        chatId: ChatId,
        messageId: Long,
    ): TelegramBotResult<Boolean> = service.deleteAllMessageReactions(chatId, messageId).runApiOperation()

    // --- Bot API 7.5 / 7.6 — Stars + paid media ---

    fun getStarTransactions(
        offset: Long? = null,
        limit: Int? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.payments.StarTransactions> =
        service.getStarTransactions(offset, limit).runApiOperation()

    fun sendPaidMedia(
        chatId: ChatId,
        starCount: Int,
        media: List<com.github.kotlintelegrambot.entities.payments.InputPaidMedia>,
        payload: String? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyParameters: com.github.kotlintelegrambot.entities.ReplyParameters? = null,
        businessConnectionId: String? = null,
        allowPaidBroadcast: Boolean? = null,
    ): TelegramBotResult<Message> = service.sendPaidMedia(
        chatId,
        starCount,
        gson.toJson(media),
        payload,
        caption,
        parseMode,
        if (captionEntities != null) gson.toJson(captionEntities) else null,
        showCaptionAboveMedia,
        disableNotification,
        protectContent,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        businessConnectionId,
        allowPaidBroadcast,
    ).runApiOperation()

    // --- Bot API 8.0 — Gifts ---

    fun getAvailableGifts(): TelegramBotResult<com.github.kotlintelegrambot.entities.gifts.Gifts> =
        service.getAvailableGifts().runApiOperation()

    fun sendGift(
        giftId: String,
        userId: Long? = null,
        chatId: ChatId? = null,
        payForUpgrade: Boolean? = null,
        text: String? = null,
        textParseMode: ParseMode? = null,
        textEntities: List<MessageEntity>? = null,
    ): TelegramBotResult<Boolean> = service.sendGift(
        userId,
        chatId,
        giftId,
        payForUpgrade,
        text,
        textParseMode,
        if (textEntities != null) gson.toJson(textEntities) else null,
    ).runApiOperation()

    fun giftPremiumSubscription(
        userId: Long,
        monthCount: Int,
        starCount: Int,
        text: String? = null,
        textParseMode: ParseMode? = null,
        textEntities: List<MessageEntity>? = null,
    ): TelegramBotResult<Boolean> = service.giftPremiumSubscription(
        userId,
        monthCount,
        starCount,
        text,
        textParseMode,
        if (textEntities != null) gson.toJson(textEntities) else null,
    ).runApiOperation()

    fun setUserEmojiStatus(
        userId: Long,
        emojiStatusCustomEmojiId: String? = null,
        emojiStatusExpirationDate: Long? = null,
    ): TelegramBotResult<Boolean> = service.setUserEmojiStatus(
        userId,
        emojiStatusCustomEmojiId,
        emojiStatusExpirationDate,
    ).runApiOperation()

    fun savePreparedInlineMessage(
        userId: Long,
        result: com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult,
        allowUserChats: Boolean? = null,
        allowBotChats: Boolean? = null,
        allowGroupChats: Boolean? = null,
        allowChannelChats: Boolean? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.PreparedInlineMessage> =
        service.savePreparedInlineMessage(
            userId,
            gson.toJson(result),
            allowUserChats,
            allowBotChats,
            allowGroupChats,
            allowChannelChats,
        ).runApiOperation()

    // --- Bot API 8.2 — Verification ---

    fun verifyUser(userId: Long, customDescription: String? = null): TelegramBotResult<Boolean> =
        service.verifyUser(userId, customDescription).runApiOperation()

    fun verifyChat(chatId: ChatId, customDescription: String? = null): TelegramBotResult<Boolean> =
        service.verifyChat(chatId, customDescription).runApiOperation()

    fun removeUserVerification(userId: Long): TelegramBotResult<Boolean> =
        service.removeUserVerification(userId).runApiOperation()

    fun removeChatVerification(chatId: ChatId): TelegramBotResult<Boolean> =
        service.removeChatVerification(chatId).runApiOperation()

    // --- Bot API 9.0 — Business account management ---

    fun readBusinessMessage(
        businessConnectionId: String,
        chatId: ChatId,
        messageId: Long,
    ): TelegramBotResult<Boolean> =
        service.readBusinessMessage(businessConnectionId, chatId, messageId).runApiOperation()

    fun deleteBusinessMessages(
        businessConnectionId: String,
        messageIds: List<Long>,
    ): TelegramBotResult<Boolean> =
        service.deleteBusinessMessages(businessConnectionId, gson.toJson(messageIds)).runApiOperation()

    fun setBusinessAccountName(
        businessConnectionId: String,
        firstName: String,
        lastName: String? = null,
    ): TelegramBotResult<Boolean> =
        service.setBusinessAccountName(businessConnectionId, firstName, lastName).runApiOperation()

    fun setBusinessAccountUsername(
        businessConnectionId: String,
        username: String? = null,
    ): TelegramBotResult<Boolean> =
        service.setBusinessAccountUsername(businessConnectionId, username).runApiOperation()

    fun setBusinessAccountBio(
        businessConnectionId: String,
        bio: String? = null,
    ): TelegramBotResult<Boolean> = service.setBusinessAccountBio(businessConnectionId, bio).runApiOperation()

    fun setBusinessAccountProfilePhoto(
        businessConnectionId: String,
        photo: com.github.kotlintelegrambot.entities.InputProfilePhoto,
        isPublic: Boolean? = null,
    ): TelegramBotResult<Boolean> =
        service.setBusinessAccountProfilePhoto(businessConnectionId, gson.toJson(photo), isPublic).runApiOperation()

    fun removeBusinessAccountProfilePhoto(
        businessConnectionId: String,
        isPublic: Boolean? = null,
    ): TelegramBotResult<Boolean> =
        service.removeBusinessAccountProfilePhoto(businessConnectionId, isPublic).runApiOperation()

    fun setBusinessAccountGiftSettings(
        businessConnectionId: String,
        showGiftButton: Boolean,
        acceptedGiftTypes: com.github.kotlintelegrambot.entities.gifts.AcceptedGiftTypes,
    ): TelegramBotResult<Boolean> = service.setBusinessAccountGiftSettings(
        businessConnectionId,
        showGiftButton,
        gson.toJson(acceptedGiftTypes),
    ).runApiOperation()

    fun getBusinessAccountStarBalance(
        businessConnectionId: String,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.payments.StarAmount> =
        service.getBusinessAccountStarBalance(businessConnectionId).runApiOperation()

    fun transferBusinessAccountStars(
        businessConnectionId: String,
        starCount: Int,
    ): TelegramBotResult<Boolean> =
        service.transferBusinessAccountStars(businessConnectionId, starCount).runApiOperation()

    fun getBusinessAccountGifts(
        businessConnectionId: String,
        excludeUnsaved: Boolean? = null,
        excludeSaved: Boolean? = null,
        excludeUnlimited: Boolean? = null,
        excludeLimited: Boolean? = null,
        excludeUnique: Boolean? = null,
        sortByPrice: Boolean? = null,
        offset: String? = null,
        limit: Int? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.gifts.OwnedGifts> =
        service.getBusinessAccountGifts(
            businessConnectionId, excludeUnsaved, excludeSaved, excludeUnlimited,
            excludeLimited, excludeUnique, sortByPrice, offset, limit,
        ).runApiOperation()

    fun convertGiftToStars(
        businessConnectionId: String,
        ownedGiftId: String,
    ): TelegramBotResult<Boolean> =
        service.convertGiftToStars(businessConnectionId, ownedGiftId).runApiOperation()

    fun upgradeGift(
        businessConnectionId: String,
        ownedGiftId: String,
        keepOriginalDetails: Boolean? = null,
        starCount: Int? = null,
    ): TelegramBotResult<Boolean> =
        service.upgradeGift(businessConnectionId, ownedGiftId, keepOriginalDetails, starCount).runApiOperation()

    fun transferGift(
        businessConnectionId: String,
        ownedGiftId: String,
        newOwnerChatId: Long,
        starCount: Int? = null,
    ): TelegramBotResult<Boolean> =
        service.transferGift(businessConnectionId, ownedGiftId, newOwnerChatId, starCount).runApiOperation()

    fun postStory(
        businessConnectionId: String,
        content: com.github.kotlintelegrambot.entities.stories.InputStoryContent,
        activePeriod: Int,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<com.github.kotlintelegrambot.entities.stories.StoryArea>? = null,
        postToChatPage: Boolean? = null,
        protectContent: Boolean? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.Story> = service.postStory(
        businessConnectionId, gson.toJson(content), activePeriod, caption, parseMode,
        if (captionEntities != null) gson.toJson(captionEntities) else null,
        if (areas != null) gson.toJson(areas) else null,
        postToChatPage, protectContent,
    ).runApiOperation()

    fun editStory(
        businessConnectionId: String,
        storyId: Int,
        content: com.github.kotlintelegrambot.entities.stories.InputStoryContent,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<com.github.kotlintelegrambot.entities.stories.StoryArea>? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.Story> = service.editStory(
        businessConnectionId,
        storyId,
        gson.toJson(content),
        caption,
        parseMode,
        if (captionEntities != null) gson.toJson(captionEntities) else null,
        if (areas != null) gson.toJson(areas) else null,
    ).runApiOperation()

    fun deleteStory(
        businessConnectionId: String,
        storyId: Int,
    ): TelegramBotResult<Boolean> =
        service.deleteStory(businessConnectionId, storyId).runApiOperation()

    // --- Bot API 9.1 — Checklists ---

    fun sendChecklist(
        businessConnectionId: String,
        chatId: ChatId,
        checklist: com.github.kotlintelegrambot.entities.checklists.InputChecklist,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: com.github.kotlintelegrambot.entities.ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): TelegramBotResult<Message> = service.sendChecklist(
        businessConnectionId,
        chatId,
        gson.toJson(checklist),
        disableNotification,
        protectContent,
        messageEffectId,
        if (replyParameters != null) gson.toJson(replyParameters) else null,
        replyMarkup,
    ).runApiOperation()

    fun editMessageChecklist(
        businessConnectionId: String,
        chatId: ChatId,
        messageId: Long,
        checklist: com.github.kotlintelegrambot.entities.checklists.InputChecklist,
        replyMarkup: ReplyMarkup? = null,
    ): TelegramBotResult<Message> = service.editMessageChecklist(
        businessConnectionId,
        chatId,
        messageId,
        gson.toJson(checklist),
        replyMarkup,
    ).runApiOperation()

    fun getMyStarBalance(): TelegramBotResult<com.github.kotlintelegrambot.entities.payments.StarAmount> =
        service.getMyStarBalance().runApiOperation()

    // --- Bot API 9.2 — Suggested posts ---

    fun approveSuggestedPost(
        chatId: ChatId,
        messageId: Long,
        sendDate: Long? = null,
    ): TelegramBotResult<Boolean> = service.approveSuggestedPost(chatId, messageId, sendDate).runApiOperation()

    fun declineSuggestedPost(
        chatId: ChatId,
        messageId: Long,
        comment: String? = null,
    ): TelegramBotResult<Boolean> = service.declineSuggestedPost(chatId, messageId, comment).runApiOperation()

    // --- Bot API 10.0 — Guest mode ---

    fun answerGuestQuery(
        guestQueryId: String,
        text: String? = null,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
    ): TelegramBotResult<com.github.kotlintelegrambot.entities.guest.SentGuestMessage> =
        service.answerGuestQuery(
            guestQueryId,
            text,
            parseMode,
            if (entities != null) gson.toJson(entities) else null,
        ).runApiOperation()

    private fun <T : Any> Call<Response<T>>.runApiOperation(): TelegramBotResult<T> {
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
        postfix = "]",
    ) { "\"$it\"" }
}
