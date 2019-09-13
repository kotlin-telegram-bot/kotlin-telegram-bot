package me.ivmg.telegram.network

import java.io.File as SystemFile
import java.net.Proxy
import java.nio.file.Files
import java.util.Date
import java.util.concurrent.TimeUnit
import me.ivmg.telegram.Poll
import me.ivmg.telegram.entities.Chat
import me.ivmg.telegram.entities.ChatAction
import me.ivmg.telegram.entities.ChatMember
import me.ivmg.telegram.entities.File
import me.ivmg.telegram.entities.InlineKeyboardMarkup
import me.ivmg.telegram.entities.Message
import me.ivmg.telegram.entities.ReplyMarkup
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.User
import me.ivmg.telegram.entities.UserProfilePhotos
import me.ivmg.telegram.entities.inputmedia.InputMedia
import me.ivmg.telegram.entities.payments.LabeledPrice
import me.ivmg.telegram.entities.payments.ShippingOption
import me.ivmg.telegram.entities.stickers.ChatPermissions
import me.ivmg.telegram.entities.stickers.MaskPosition
import me.ivmg.telegram.entities.stickers.StickerSet
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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

class ApiClient(
    token: String,
    apiUrl: String,
    private val botTimeout: Int = 30,
    logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
    proxy: Proxy = Proxy.NO_PROXY
) {
    private val service: ApiService

    // TODO check if init is the best approach for this
    init {
        val logging = HttpLoggingInterceptor().apply { level = logLevel }

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .readTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .writeTimeout(botTimeout + 10L, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .proxy(proxy)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$apiUrl$token/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
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
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): Call<Response<Message>> {

        return service.sendPhoto(
            convertString(chatId.toString()),
            convertFile("photo", photo),
            if (caption != null) convertString(caption) else null,
            if (parseMode != null) convertString(parseMode) else null,
            if (disableNotification != null) convertString(disableNotification.toString()) else null,
            if (replyToMessageId != null) convertString(replyToMessageId.toString()) else null
        )
    }

    fun sendPhoto(
        chatId: Long,
        photo: String,
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): Call<Response<Message>> {

        return service.sendPhoto(
            chatId,
            photo,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId
        )
    }

    fun sendAudio(
        chatId: Long,
        audio: SystemFile,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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

    fun sendVideo(
        chatId: Long,
        video: SystemFile,
        duration: Int?,
        width: Int?,
        height: Int?,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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

    fun sendVideoNote(
        chatId: Long,
        audio: SystemFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>> {

        return service.sendVideoNote(
            convertString(chatId.toString()),
            convertFile("video_note", audio),
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
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
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

    fun sendMediaGroup(
        chatId: Long,
        media: List<InputMedia>,
        disableNotification: Boolean?,
        replyToMessageId: Long?
    ): Call<Response<Message>> {

        return service.sendMediaGroup(
            chatId,
            media,
            disableNotification,
            replyToMessageId
        )
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
        disableNotification: Boolean?,
        replyToMessageId: Long?,
        replyMarkup: ReplyMarkup?
    ): Call<Response<Message>> {

        return service.sendPoll(
            chatId,
            question,
            options,
            disableNotification,
            replyToMessageId,
            replyMarkup
        )
    }

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
        providerData: String? = null,
        photoUrl: String? = null,
        photoSize: Int? = null,
        photoWidth: Int? = null,
        photoHeight: Int? = null,
        needName: Boolean? = null,
        needPhoneNumber: Boolean? = null,
        needEmail: Boolean? = null,
        needShippingAddress: Boolean? = null,
        sendPhoneNumberToProvider: Boolean? = null,
        sendEmailToProvider: Boolean? = null,
        isFlexible: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: InlineKeyboardMarkup? = null
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
        shippingOptions: List<ShippingOption>? = null,
        errorMessage: String? = null
    ) = service.answerShippingQuery(shippingQueryId, ok, shippingOptions, errorMessage)

    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String? = null
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
}
