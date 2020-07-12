package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.entities.payments.PaymentInvoiceInfo
import com.github.kotlintelegrambot.entities.payments.ShippingOption
import com.github.kotlintelegrambot.entities.polls.PollType
import com.github.kotlintelegrambot.entities.stickers.MaskPosition
import com.github.kotlintelegrambot.errors.RetrieveUpdatesError
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.network.bimap
import com.github.kotlintelegrambot.network.call
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.updater.Updater
import com.github.kotlintelegrambot.webhook.WebhookConfig
import com.github.kotlintelegrambot.webhook.WebhookConfigBuilder
import java.io.File as SystemFile
import java.net.Proxy
import java.util.Date

fun bot(body: Bot.Builder.() -> Unit): Bot = Bot.Builder().build(body)

fun Bot.Builder.dispatch(body: Dispatcher.() -> Unit) = updater.dispatcher.apply(body)

fun Bot.Builder.webhook(
    body: WebhookConfigBuilder.() -> Unit
) {
    val webhookConfigBuilder = WebhookConfigBuilder()
    webhookConfigBuilder.apply(body)
    webhookConfig = webhookConfigBuilder.build()
}

class Bot private constructor(
    private val updater: Updater,
    private val updateMapper: UpdateMapper,
    private val webhookConfig: WebhookConfig?,
    token: String,
    apiUrl: String,
    timeout: Int = 30,
    logLevel: LogLevel,
    proxy: Proxy
) {
    private val apiClient: ApiClient = ApiClient(token, apiUrl, timeout, logLevel, proxy)

    init {
        updater.bot = this
        updater.dispatcher.bot = this
        updater.dispatcher.logLevel = logLevel
    }

    class Builder {
        val updater = Updater()
        private val updateMapper = UpdateMapper()
        var webhookConfig: WebhookConfig? = null
        lateinit var token: String
        var timeout: Int = 30
        var apiUrl: String = "https://api.telegram.org/"
        var logLevel: LogLevel = LogLevel.None
        var proxy: Proxy = Proxy.NO_PROXY

        fun build(): Bot {
            return Bot(updater, updateMapper, webhookConfig, token, apiUrl, timeout, logLevel, proxy)
        }

        fun build(body: Bot.Builder.() -> Unit): Bot {
            body()
            return Bot(updater, updateMapper, webhookConfig, token, apiUrl, timeout, logLevel, proxy)
        }
    }

    fun startPolling() = updater.startPolling()

    fun stopPolling() = updater.stopPolling()

    /**
     * Starts a webhook through the setWebhook Telegram's API operation and starts checking
     * updates if it was successfully set.
     * @return true if the webhook was successfully set or false otherwise
     */
    fun startWebhook(): Boolean {
        if (webhookConfig == null) {
            error("To start a webhook you need to configure it on bot set up. Check the `webhook` builder function")
        }

        val setWebhookResult = setWebhook(
            webhookConfig.url,
            webhookConfig.certificate,
            webhookConfig.maxConnections,
            webhookConfig.allowedUpdates
        )
        val webhookSet = setWebhookResult.bimap(
            mapResponse = { true },
            mapError = { false }
        )

        if (webhookSet) {
            updater.startCheckingUpdates()
        }

        return webhookSet
    }

    /**
     * Deletes a webhook through the deleteWebhook Telegram's API operation and stops checking updates.
     * @return true if the webhook was successfully deleted or false otherwise
     */
    fun stopWebhook(): Boolean {
        if (webhookConfig == null) {
            error("To stop a webhook you need to configure it on bot set up. Check the `webhook` builder function")
        }

        updater.stopCheckingUpdates()

        val deleteWebhookResult = deleteWebhook()

        return deleteWebhookResult.bimap(
            mapResponse = { true },
            mapError = { false }
        )
    }

    fun getUpdates(offset: Long): List<DispatchableObject> {
        val call = if (offset > 0)
            apiClient.getUpdates(offset = offset)
        else
            apiClient.getUpdates()

        val (response, error) = call.call()

        when (response?.isSuccessful) {
            true -> {
                val updates = response.body()
                if (updates?.result != null) return updates.result
            }
            false, null -> {
                val errorMessage: String = when {
                    error?.message != null -> error.message!!
                    response?.errorBody() != null -> response.errorBody().toString()
                    else -> "There was a problem retrieving updates from Telegram server"
                }

                return listOf(RetrieveUpdatesError(errorMessage) as TelegramError)
            }
        }
        return emptyList()
    }

    fun setWebhook(
        url: String,
        certificate: TelegramFile? = null,
        maxConnections: Int? = null,
        allowedUpdates: List<String>? = null
    ) = apiClient.setWebhook(url, certificate, maxConnections, allowedUpdates).call()

    fun deleteWebhook() = apiClient.deleteWebhook().call()

    fun getWebhookInfo() = apiClient.getWebhookInfo().call()

    fun processUpdate(update: Update) {
        updater.dispatcher.updatesQueue.put(update)
    }

    fun processUpdate(updateJson: String) {
        val update = updateMapper.jsonToUpdate(updateJson)
        processUpdate(update)
    }

    fun getMe() = apiClient.getMe().call()

    fun sendMessage(
        chatId: Long,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendMessage(
        chatId,
        text,
        parseMode?.modeName,
        disableWebPagePreview,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun forwardMessage(
        chatId: Long,
        fromChatId: Long,
        messageId: Long,
        disableNotification: Boolean? = null
    ) = apiClient.forwardMessage(
        chatId,
        fromChatId,
        messageId,
        disableNotification
    ).call()

    fun sendPhoto(
        chatId: Long,
        photo: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendPhoto(
        chatId: Long,
        photo: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAudio(
        chatId: Long,
        audio: String,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        null,
        null,
        caption,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAudio(
        chatId: Long,
        audio: SystemFile,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        duration,
        performer,
        title,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAudio(
        chatId: Long,
        audio: String,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        duration,
        performer,
        title,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendDocument(
        chatId: Long,
        document: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        document,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendDocument(
        chatId: Long,
        fileBytes: ByteArray,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null,
        fileName: String
    ) = apiClient.sendDocument(
        chatId,
        fileBytes,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        replyMarkup,
        fileName
    ).call()

    fun sendDocument(
        chatId: Long,
        fileId: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        fileId,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideo(
        chatId: Long,
        video: SystemFile,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideo(
        chatId,
        video,
        duration,
        width,
        height,
        caption,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideo(
        chatId: Long,
        fileId: String,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideo(
        chatId,
        fileId,
        duration,
        width,
        height,
        caption,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAnimation(
        chatId: Long,
        animation: SystemFile,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAnimation(
        chatId,
        animation,
        duration,
        width,
        height,
        caption,
        parseMode,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAnimation(
        chatId: Long,
        fileId: String,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        parseMode: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAnimation(
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
    ).call()

    fun sendVoice(
        chatId: Long,
        audio: ByteArray,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audio,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVoice(
        chatId: Long,
        audio: SystemFile,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audio,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVoice(
        chatId: Long,
        audioId: String,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audioId,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideoNote(
        chatId: Long,
        videoNote: SystemFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideoNote(
        chatId,
        videoNote,
        duration,
        length,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideoNote(
        chatId: Long,
        videoNoteId: String,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideoNote(
        chatId,
        videoNoteId,
        duration,
        length,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendMediaGroup(
        chatId: String,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendMediaGroup(chatId, mediaGroup, disableNotification, replyToMessageId).call()

    fun sendMediaGroup(
        chatId: Long,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendMediaGroup(chatId, mediaGroup, disableNotification, replyToMessageId).call()

    fun sendLocation(
        chatId: Long,
        latitude: Float,
        longitude: Float,
        livePeriod: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendLocation(
        chatId,
        latitude,
        longitude,
        livePeriod,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

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
    ) = apiClient.sendPoll(
        channelUsername,
        question,
        options,
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
    ).call()

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
    ) = apiClient.sendPoll(
        chatId,
        question,
        options,
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
    ).call()

    fun editMessageLiveLocation(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.editMessageLiveLocation(
        chatId,
        messageId,
        inlineMessageId,
        latitude,
        longitude,
        replyMarkup
    ).call()

    fun stopMessageLiveLocation(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.stopMessageLiveLocation(
        chatId,
        messageId,
        inlineMessageId,
        replyMarkup
    ).call()

    fun sendVenue(
        chatId: Long,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String? = null,
        foursquareType: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVenue(
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
    ).call()

    fun sendContact(
        chatId: Long,
        phoneNumber: String,
        firstName: String,
        lastName: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendContact(
        chatId,
        phoneNumber,
        firstName,
        lastName,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendChatAction(chatId: Long, action: ChatAction) =
        apiClient.sendChatAction(chatId, action).call()

    fun getUserProfilePhotos(userId: Long, offset: Long? = null, limit: Int? = null) =
        apiClient.getUserProfilePhotos(userId, offset, limit).call()

    fun getFile(fileId: String) = apiClient.getFile(fileId).call()

    fun downloadFile(filePath: String) = apiClient.downloadFile(filePath).call()

    fun downloadFileBytes(fileId: String): ByteArray? {
        val fileResp = getFile(fileId).first
        return if (fileResp?.isSuccessful == true) {
            val filePath = fileResp.body()?.result?.filePath
            if (filePath == null) null else downloadFile(filePath).first?.body()?.bytes()
        } else {
            null
        }
    }

    fun kickChatMember(chatId: Long, userId: Long, untilDate: Date) =
        apiClient.kickChatMember(chatId, userId, untilDate).call()

    fun unbanChatMember(chatId: Long, userId: Long) =
        apiClient.unbanChatMember(chatId, userId).call()

    fun restrictChatMember(
        chatId: Long,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Date? = null
    ) = apiClient.restrictChatMember(
        chatId,
        userId,
        chatPermissions,
        untilDate
    ).call()

    fun promoteChatMember(
        chatId: Long,
        userId: Long,
        canChangeInfo: Boolean? = null,
        canPostMessages: Boolean? = null,
        canEditMessages: Boolean? = null,
        canDeleteMessages: Boolean? = null,
        canInviteUsers: Boolean? = null,
        canRestrictMembers: Boolean? = null,
        canPinMessages: Boolean? = null,
        canPromoteMembers: Boolean? = null
    ) = apiClient.promoteChatMember(
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
    ).call()

    fun setChatPermissions(chatId: Long, permissions: ChatPermissions) =
        apiClient.setChatPermissions(chatId, permissions).call()

    fun exportChatInviteLink(chatId: Long) = apiClient.exportChatInviteLink(chatId).call()

    fun setChatPhoto(
        chatId: Long,
        photo: SystemFile
    ) =
        apiClient.setChatPhoto(chatId, photo).call()

    fun deleteChatPhoto(chatId: Long) = apiClient.deleteChatPhoto(chatId).call()

    fun setChatTitle(chatId: Long, title: String) = apiClient.setChatTitle(chatId, title).call()

    fun setChatDescription(chatId: Long, description: String) =
        apiClient.setChatDescription(chatId, description).call()

    fun pinChatMessage(chatId: Long, messageId: Long, disableNotification: Boolean? = null) =
        apiClient.pinChatMessage(chatId, messageId, disableNotification).call()

    fun unpinChatMessage(chatId: Long) = apiClient.unpinChatMessage(chatId).call()

    fun leaveChat(chatId: Long) = apiClient.leaveChat(chatId).call()

    fun getChat(chatId: Long) = apiClient.getChat(chatId).call()

    fun getChatAdministrators(chatId: Long) = apiClient.getChatAdministrators(chatId).call()

    fun getChatMembersCount(chatId: Long) = apiClient.getChatMembersCount(chatId).call()

    fun getChatMember(chatId: Long, userId: Long) = apiClient.getChatMember(chatId, userId).call()

    fun setChatStickerSet(chatId: Long, stickerSetName: String) =
        apiClient.setChatStickerSet(chatId, stickerSetName).call()

    fun deleteChatStickerSet(chatId: Long) = apiClient.deleteChatStickerSet(chatId).call()

    fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean? = null,
        url: String? = null,
        cacheTime: Int? = null
    ) = apiClient.answerCallbackQuery(
        callbackQueryId,
        text,
        showAlert,
        url,
        cacheTime
    ).call()

    /**
     * Updating messages
     */

    fun editMessageText(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.editMessageText(
        chatId,
        messageId,
        inlineMessageId,
        text,
        parseMode?.modeName,
        disableWebPagePreview,
        replyMarkup
    ).call()

    fun editMessageCaption(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        caption: String,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.editMessageCaption(
        chatId,
        messageId,
        inlineMessageId,
        caption,
        replyMarkup
    ).call()

    fun editMessageMedia(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        media: InputMedia,
        replyMarkup: ReplyMarkup?
    ) = apiClient.editMessageMedia(
        chatId,
        messageId,
        inlineMessageId,
        media,
        replyMarkup
    ).call()

    fun editMessageReplyMarkup(
        chatId: Long? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.editMessageReplyMarkup(
        chatId,
        messageId,
        inlineMessageId,
        replyMarkup
    ).call()

    fun deleteMessage(chatId: Long? = null, messageId: Long? = null) =
        apiClient.deleteMessage(chatId, messageId).call()

    /***
     * Stickers
     */

    fun sendSticker(
        chatId: Long,
        sticker: SystemFile,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup?
    ) = apiClient.sendSticker(
        chatId,
        sticker,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendSticker(
        chatId: Long,
        sticker: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup?
    ) = apiClient.sendSticker(
        chatId,
        sticker,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun getStickerSet(
        name: String
    ) = apiClient.getStickerSet(name).call()

    fun uploadStickerFile(
        userId: Long,
        pngSticker: SystemFile
    ) = apiClient.uploadStickerFile(
        userId,
        pngSticker
    ).call()

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: SystemFile,
        emojis: String,
        containsMasks: Boolean? = null,
        maskPosition: MaskPosition?
    ) = apiClient.createNewStickerSet(
        userId,
        name,
        title,
        pngSticker,
        emojis,
        containsMasks,
        maskPosition
    ).call()

    fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: String,
        emojis: String,
        containsMasks: Boolean? = null,
        maskPosition: MaskPosition?
    ) = apiClient.createNewStickerSet(
        userId,
        name,
        title,
        pngSticker,
        emojis,
        containsMasks,
        maskPosition
    ).call()

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: SystemFile,
        emojis: String,
        maskPosition: MaskPosition?
    ) = apiClient.addStickerToSet(
        userId,
        name,
        pngSticker,
        emojis,
        maskPosition
    ).call()

    fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: String,
        emojis: String,
        maskPosition: MaskPosition?
    ) = apiClient.addStickerToSet(
        userId,
        name,
        pngSticker,
        emojis,
        maskPosition
    ).call()

    fun setStickerPositionInSet(
        sticker: String,
        position: Int
    ) = apiClient.setStickerPositionInSet(
        sticker,
        position
    ).call()

    fun deleteStickerFromSet(
        sticker: String
    ) = apiClient.deleteStickerFromSet(
        sticker
    ).call()

    /**
     * Payments
     */

    /**
     * Use this method to send invoices.
     *
     * @param [chatId] Unique identifier for the target private chat.
     * @param [disableNotification] Sends the message silently. Users will receive a notification with no sound.
     * @param [replyToMessageId] If the message is a reply, ID of the original message.
     * @param [replyMarkup] Additional interface options. An inlineKeyboard. If empty, one 'Pay total price' button will be shown. If not empty, the first button must be a Pay button.
     * @see InlineKeyboardMarkup
     * @see PaymentInvoiceInfo
     */
    fun sendInvoice(
        chatId: Long,
        paymentInvoiceInfo: PaymentInvoiceInfo,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: InlineKeyboardMarkup? = null
    ) = apiClient.sendInvoice(
        chatId,
        paymentInvoiceInfo.title,
        paymentInvoiceInfo.description,
        paymentInvoiceInfo.payload,
        paymentInvoiceInfo.providerToken,
        paymentInvoiceInfo.startParameter,
        paymentInvoiceInfo.currency,
        paymentInvoiceInfo.prices,
        isFlexible = paymentInvoiceInfo.isFlexible,
        providerData = paymentInvoiceInfo.providerData,
        needShippingAddress = paymentInvoiceInfo.invoiceUserDetail?.needShippingAddress,
        needPhoneNumber = paymentInvoiceInfo.invoiceUserDetail?.needPhoneNumber,
        needName = paymentInvoiceInfo.invoiceUserDetail?.needName,
        needEmail = paymentInvoiceInfo.invoiceUserDetail?.needEmail,
        sendPhoneNumberToProvider = paymentInvoiceInfo.invoiceUserDetail?.sendPhoneNumberToProvider,
        sendEmailToProvider = paymentInvoiceInfo.invoiceUserDetail?.sendEmailToProvider,
        photoWidth = paymentInvoiceInfo.invoicePhoto?.photoWidth,
        photoUrl = paymentInvoiceInfo.invoicePhoto?.photoUrl,
        photoSize = paymentInvoiceInfo.invoicePhoto?.photoSize,
        photoHeight = paymentInvoiceInfo.invoicePhoto?.photoHeight,
        replyToMessageId = replyToMessageId,
        disableNotification = disableNotification,
        replyMarkup = replyMarkup
    ).call()

    fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>? = null,
        errorMessage: String? = null
    ) = apiClient.answerShippingQuery(
        shippingQueryId,
        ok,
        shippingOptions,
        errorMessage
    ).call()

    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String? = null
    ) = apiClient.answerPreCheckoutQuery(
        preCheckoutQueryId,
        ok,
        errorMessage
    ).call()

    fun answerInlineQuery(
        inlineQueryId: String,
        vararg inlineQueryResults: InlineQueryResult,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null
    ) = answerInlineQuery(
        inlineQueryId,
        inlineQueryResults.toList(),
        cacheTime,
        isPersonal,
        nextOffset,
        switchPmText,
        switchPmParameter
    )

    fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null
    ) = apiClient.answerInlineQuery(
        inlineQueryId,
        inlineQueryResults,
        cacheTime,
        isPersonal,
        nextOffset,
        switchPmText,
        switchPmParameter
    ).call()

    fun setMyCommands(
        commands: List<BotCommand>
    ) = apiClient.setMyCommands(
        commands
    ).call()

    fun getMyCommands() = apiClient.getMyCommands().call()

    /**
     * Use this method to send a dice, which will have a random value from 1 to 6.
     * @param chatId Unique identifier for the target chat
     * @param emoji Emoji on which the dice throw animation is based. Currently, must be one of 🎲 or 🎯. Defaults to 🎲
     * @param disableNotification Sends the message silently. Users will receive a notification with no sound
     * @param replyToMessageId If the message is a reply, ID of the original message
     * @param replyMarkup A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to remove reply keyboard or to force a reply from the user
     * @return the sent Message
     */
    fun sendDice(
        chatId: Long,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDice(
        chatId,
        emoji,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    /**
     * Use this method to send a dice, which will have a random value from 1 to 6.
     * @param channelUsername Username of the target channel (in the format @channelusername)
     * @param emoji Emoji on which the dice throw animation is based. Currently, must be one of 🎲 or 🎯. Defaults to 🎲
     * @param disableNotification Sends the message silently. Users will receive a notification with no sound
     * @param replyToMessageId If the message is a reply, ID of the original message
     * @param replyMarkup A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to remove reply keyboard or to force a reply from the user
     * @return the sent Message
     */
    fun sendDice(
        channelUsername: String,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDice(
        channelUsername,
        emoji,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    /**
     * Use this method to set a custom title for an administrator in a supergroup promoted by the bot.
     * @param chatId Unique identifier for the target chat
     * @param userId Unique identifier of the target user
     * @param customTitle New custom title for the administrator; 0-16 characters, emoji are not allowed
     * @return true on success.
     */
    fun setChatAdministratorCustomTitle(chatId: Long, userId: Long, customTitle: String) =
        apiClient.setChatAdministratorCustomTitle(
            chatId,
            userId,
            customTitle
        ).call()

    /**
     * Use this method to set a custom title for an administrator in a supergroup promoted by the bot.
     * @param channelUsername Username of the target channel (in the format @channelusername)
     * @param userId Unique identifier of the target user
     * @param customTitle New custom title for the administrator; 0-16 characters, emoji are not allowed
     * @return true on success.
     */
    fun setChatAdministratorCustomTitle(channelUsername: String, userId: Long, customTitle: String) =
        apiClient.setChatAdministratorCustomTitle(
            channelUsername,
            userId,
            customTitle
        ).call()
}
