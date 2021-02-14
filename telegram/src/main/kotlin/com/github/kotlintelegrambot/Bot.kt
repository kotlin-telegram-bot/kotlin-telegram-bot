package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity
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
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import com.github.kotlintelegrambot.updater.Updater
import com.github.kotlintelegrambot.webhook.WebhookConfig
import com.github.kotlintelegrambot.webhook.WebhookConfigBuilder
import com.google.gson.Gson
import java.net.Proxy
import java.io.File as SystemFile

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
    proxy: Proxy,
    gson: Gson
) {

    private val apiClient: ApiClient = ApiClient(token, apiUrl, timeout, logLevel, proxy, gson)

    init {
        updater.bot = this
        updater.dispatcher.bot = this
        updater.dispatcher.logLevel = logLevel
    }

    class Builder {
        val updater = Updater()
        private val gson = GsonFactory.createForApiClient()
        private val updateMapper = UpdateMapper(gson)
        var webhookConfig: WebhookConfig? = null
        lateinit var token: String
        var timeout: Int = 30
        var apiUrl: String = "https://api.telegram.org/"
        var logLevel: LogLevel = LogLevel.None
        var proxy: Proxy = Proxy.NO_PROXY

        fun build(): Bot {
            return Bot(updater, updateMapper, webhookConfig, token, apiUrl, timeout, logLevel, proxy, gson)
        }

        fun build(body: Bot.Builder.() -> Unit): Bot {
            body()
            return Bot(updater, updateMapper, webhookConfig, token, apiUrl, timeout, logLevel, proxy, gson)
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
                fun dispatchableError(message: String) =
                    listOf<TelegramError>(RetrieveUpdatesError(message))

                val errorMessage = error?.message
                if (errorMessage != null) {
                    return dispatchableError(errorMessage)
                }

                val errorBody = response?.errorBody()?.string()
                if (errorBody != null && errorBody.isNotBlank()) {
                    return dispatchableError(errorBody)
                }

                val rawHttpResponse = response?.raw()
                if (rawHttpResponse != null && rawHttpResponse.message().isNotBlank()) {
                    return dispatchableError(
                        "${rawHttpResponse.code()} - ${rawHttpResponse.message()}"
                    )
                }

                return dispatchableError(
                    "There was a problem retrieving updates from Telegram server"
                )
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

    /**
     * Use this method to send text messages
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername)
     * @param text text of the message to be sent, 1-4096 characters after entities parsing
     * @param parseMode mode for parsing entities in the message text
     * @param disableWebPagePreview disables link previews for links in this message
     * @param disableNotification sends the message silently - users will receive a notification with no sound
     * @param replyToMessageId if the message is a reply, ID of the original message
     * @param replyMarkup additional options - inline keyboard, custom reply keyboard, instructions to remove reply
     * keyboard or to force a reply from the user
     * @return the sent [Message] on success
     */
    fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendMessage(
        chatId,
        text,
        parseMode,
        disableWebPagePreview,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        disableNotification: Boolean? = null
    ) = apiClient.forwardMessage(
        chatId,
        fromChatId,
        messageId,
        disableNotification
    ).call()

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
    ) = apiClient.copyMessage(
        chatId,
        fromChatId,
        messageId,
        caption,
        parseMode,
        captionEntities,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendPhoto(
        chatId: ChatId,
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
        chatId: ChatId,
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
        parseMode,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendAudio(
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
        parseMode,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideo(
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
        fileId: String,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
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
        chatId: ChatId,
        audio: ByteArray,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audio,
        caption,
        parseMode,
        captionEntities,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVoice(
        chatId: ChatId,
        audio: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audio,
        caption,
        parseMode,
        captionEntities,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVoice(
        chatId: ChatId,
        audioId: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVoice(
        chatId,
        audioId,
        caption,
        parseMode,
        captionEntities,
        duration,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendVideoNote(
        chatId: ChatId,
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
        chatId: ChatId,
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

    /**
     * Use this method to send a group of photos, videos, documents or audios as an album.
     * Documents and audio files can be only grouped in an album with messages of the same type.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername).
     * @param mediaGroup An object describing photos and videos to be sent, must include 2-10 items.
     * @param disableNotification Sends the messages silently. Users will receive a notification with no sound.
     * @param replyToMessageId If the messages are a reply, ID of the original message.
     *
     * @return a list of the sent Messages.
     */
    fun sendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): TelegramBotResult<List<Message>> = apiClient.sendMediaGroup(
        chatId,
        mediaGroup,
        disableNotification,
        replyToMessageId
    )

    fun sendLocation(
        chatId: ChatId,
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
        chatId: ChatId? = null,
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
        chatId: ChatId? = null,
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
        chatId: ChatId,
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
        chatId: ChatId,
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

    fun sendChatAction(chatId: ChatId, action: ChatAction) =
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

    fun kickChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Long? = null // unix time - https://en.wikipedia.org/wiki/Unix_time
    ) = apiClient.kickChatMember(chatId, userId, untilDate).call()

    fun unbanChatMember(chatId: ChatId, userId: Long) =
        apiClient.unbanChatMember(chatId, userId).call()

    fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Long? = null // unix time - https://en.wikipedia.org/wiki/Unix_time
    ) = apiClient.restrictChatMember(
        chatId,
        userId,
        chatPermissions,
        untilDate
    ).call()

    /**
     * Use this method to promote or demote a user in a supergroup or a channel. The bot must be
     * an administrator in the chat for this to work and must have the appropriate admin rights.
     * Pass False for all boolean parameters to demote a user.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     * @param userId Unique identifier of the target user.
     * @param isAnonymous Pass True, if the administrator's presence in the chat is hidden.
     * @param canChangeInfo Pass True, if the administrator can change chat title, photo and other
     * settings.
     * @param canPostMessages Pass True, if the administrator can create channel posts, channels only.
     * @param canEditMessages Pass True, if the administrator can edit messages of other users
     * and can pin messages, channels only.
     * @param canDeleteMessages Pass True, if the administrator can delete messages of other users.
     * @param canInviteUsers Pass True, if the administrator can invite new users to the chat.
     * @param canRestrictMembers Pass True, if the administrator can restrict, ban or unban chat
     * members.
     * @param canPinMessages Pass True, if the administrator can pin messages, supergroups only.
     * @param canPromoteMembers Pass True, if the administrator can add new administrators with a
     * subset of their own privileges or demote administrators that he has promoted, directly or
     * indirectly (promoted by administrators that were appointed by him).
     *
     * @return True on success.
     */
    fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        isAnonymous: Boolean? = null,
        canChangeInfo: Boolean? = null,
        canPostMessages: Boolean? = null,
        canEditMessages: Boolean? = null,
        canDeleteMessages: Boolean? = null,
        canInviteUsers: Boolean? = null,
        canRestrictMembers: Boolean? = null,
        canPinMessages: Boolean? = null,
        canPromoteMembers: Boolean? = null
    ): TelegramBotResult<Boolean> = apiClient.promoteChatMember(
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

    fun setChatPermissions(chatId: ChatId, permissions: ChatPermissions) =
        apiClient.setChatPermissions(chatId, permissions).call()

    fun exportChatInviteLink(chatId: ChatId) = apiClient.exportChatInviteLink(chatId).call()

    fun setChatPhoto(
        chatId: ChatId,
        photo: SystemFile
    ) =
        apiClient.setChatPhoto(chatId, photo).call()

    fun deleteChatPhoto(chatId: ChatId) = apiClient.deleteChatPhoto(chatId).call()

    fun setChatTitle(chatId: ChatId, title: String) = apiClient.setChatTitle(chatId, title).call()

    fun setChatDescription(chatId: ChatId, description: String) =
        apiClient.setChatDescription(chatId, description).call()

    fun pinChatMessage(chatId: ChatId, messageId: Long, disableNotification: Boolean? = null) =
        apiClient.pinChatMessage(chatId, messageId, disableNotification).call()

    /**
     * Use this method to remove a message from the list of pinned messages in a chat. If the chat
     * is not a private chat, the bot must be an administrator in the chat for this to work and
     * must have the 'can_pin_messages' admin right in a supergroup or 'can_edit_messages' admin
     * right in a channel.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername)
     * @param messageId Identifier of a message to unpin. If not specified, the most recent pinned
     * message (by sending date) will be unpinned.
     *
     * @return True on success.
     */
    fun unpinChatMessage(
        chatId: ChatId,
        messageId: Long? = null
    ): TelegramBotResult<Boolean> = apiClient.unpinChatMessage(chatId, messageId)

    /**
     * Use this method to clear the list of pinned messages in a chat. If the chat is not a private
     * chat, the bot must be an administrator in the chat for this to work and must have the
     * 'can_pin_messages' admin right in a supergroup or 'can_edit_messages' admin right in a
     * channel.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     *
     * @return True on success.
     */
    fun unpinAllChatMessages(
        chatId: ChatId
    ): TelegramBotResult<Boolean> = apiClient.unpinAllChatMessages(chatId)

    fun leaveChat(chatId: ChatId) = apiClient.leaveChat(chatId).call()

    fun getChat(chatId: ChatId) = apiClient.getChat(chatId).call()

    /**
     * Use this method to get a list of administrators in a chat. If the chat is a
     * group or a supergroup and no administrators were appointed, only the creator will be
     * returned.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     *
     * @return A list of [ChatMember] objects that contains information about all chat
     * administrators except other bots.
     */
    fun getChatAdministrators(
        chatId: ChatId
    ): TelegramBotResult<List<ChatMember>> = apiClient.getChatAdministrators(chatId)

    fun getChatMembersCount(chatId: ChatId) = apiClient.getChatMembersCount(chatId).call()

    fun getChatMember(chatId: ChatId, userId: Long) = apiClient.getChatMember(chatId, userId).call()

    fun setChatStickerSet(chatId: ChatId, stickerSetName: String) =
        apiClient.setChatStickerSet(chatId, stickerSetName).call()

    fun deleteChatStickerSet(chatId: ChatId) = apiClient.deleteChatStickerSet(chatId).call()

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
        chatId: ChatId? = null,
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
        parseMode,
        disableWebPagePreview,
        replyMarkup
    ).call()

    fun editMessageCaption(
        chatId: ChatId? = null,
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
        chatId: ChatId? = null,
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
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.editMessageReplyMarkup(
        chatId,
        messageId,
        inlineMessageId,
        replyMarkup
    ).call()

    /**
     * Use this method to delete a message, including service messages, with the following limitations:
     * - A message can only be deleted if it was sent less than 48 hours ago.
     * - A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.
     * - Bots can delete outgoing messages in private chats, groups, and supergroups.
     * - Bots can delete incoming messages in private chats.
     * - Bots granted `can_post_messages` permissions can delete outgoing messages in channels.
     * - If the bot is an administrator of a group, it can delete any message there.
     * - If the bot has `can_delete_messages` permission in a supergroup or a channel, it can delete any message there.
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername)
     * @param messageId Identifier of the message to delete.
     * @return True on success.
     */
    fun deleteMessage(chatId: ChatId, messageId: Long) =
        apiClient.deleteMessage(chatId, messageId).call()

    /***
     * Stickers
     */

    fun sendSticker(
        chatId: ChatId,
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
        chatId: ChatId,
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
        chatId: ChatId,
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
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername)
     * @param emoji Emoji on which the dice throw animation is based. Currently, must be one of 🎲 or 🎯. Defaults to 🎲
     * @param disableNotification Sends the message silently. Users will receive a notification with no sound
     * @param replyToMessageId If the message is a reply, ID of the original message
     * @param replyMarkup A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to remove reply keyboard or to force a reply from the user
     * @return the sent Message
     */
    fun sendDice(
        chatId: ChatId,
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
     * Use this method to set a custom title for an administrator in a supergroup promoted by the bot.
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername)
     * @param userId Unique identifier of the target user
     * @param customTitle New custom title for the administrator; 0-16 characters, emoji are not allowed
     * @return true on success.
     */
    fun setChatAdministratorCustomTitle(chatId: ChatId, userId: Long, customTitle: String) =
        apiClient.setChatAdministratorCustomTitle(
            chatId,
            userId,
            customTitle
        ).call()
}
