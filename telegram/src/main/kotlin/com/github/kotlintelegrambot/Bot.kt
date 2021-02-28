package com.github.kotlintelegrambot

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.Chat
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
        internal val updater = Updater()
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

        fun build(body: Builder.() -> Unit): Bot {
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

    /**
     * Use this method to receive incoming updates using long polling. It's recommended to use the
     * approach provided by the library to get updates using polling, but this method is offered
     * for flexibility.
     *
     * @param offset Identifier of the first update to be returned. Must be greater by one than the
     * highest among the identifiers of previously received updates. By default, updates starting
     * with the earliest unconfirmed update are returned. An update is considered confirmed as soon
     * as [getUpdates] is called with an offset higher than its update_id. The negative offset can
     * be specified to retrieve updates starting from -[offset] update from the end of the updates
     * queue. All previous updates will forgotten.
     * @param limit Limits the number of updates to be retrieved. Values between 1-100 are
     * accepted. Defaults to 100.
     * @param timeout Timeout in seconds for long polling. Defaults to 0, i.e. usual short polling.
     * Should be positive, short polling should be used for testing purposes only.
     * @param allowedUpdates List of the update types you want your bot to receive. For example,
     * specify [“message”, “edited_channel_post”, “callback_query”] to only receive updates of
     * these types. See [Update] for a complete list of available update types. Specify an empty
     * list to receive all updates regardless of type (default). If not specified, the previous
     * setting will be used. Please note that this parameter doesn't affect updates created before
     * the call to the [getUpdates], so unwanted updates may be received for a short period of time.
     *
     * @return A list of [Update] objects is returned.
     */
    fun getUpdates(
        offset: Long? = null,
        limit: Int? = null,
        timeout: Int? = null,
        allowedUpdates: List<String>? = null,
    ): TelegramBotResult<List<Update>> = apiClient.getUpdates(
        offset = offset,
        limit = limit,
        timeout = timeout,
        allowedUpdates = allowedUpdates,
    )

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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendMessage(
        chatId,
        text,
        parseMode,
        disableWebPagePreview,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendPhoto(
        chatId: ChatId,
        photo: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        parseMode,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendAudio(
        chatId: ChatId,
        audio: String,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        null,
        null,
        caption,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        duration,
        performer,
        title,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendAudio(
        chatId,
        audio,
        duration,
        performer,
        title,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendDocument(
        chatId: ChatId,
        document: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        document,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendDocument(
        chatId: ChatId,
        fileBytes: ByteArray,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        fileName: String
    ) = apiClient.sendDocument(
        chatId,
        fileBytes,
        caption,
        parseMode?.modeName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        fileId,
        caption,
        parseMode,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendVideoNote(
        chatId: ChatId,
        videoNote: SystemFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideoNote(
        chatId,
        videoNote,
        duration,
        length,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: String,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideoNote(
        chatId,
        videoNoteId,
        duration,
        length,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendLocation(
        chatId,
        latitude,
        longitude,
        livePeriod,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendContact(
        chatId,
        phoneNumber,
        firstName,
        lastName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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

    /**
     * Use this method to unban a previously kicked user in a supergroup or channel. The user will
     * not return to the group or channel automatically, but will be able to join via link, etc.
     * The bot must be an administrator for this to work. By default, this method guarantees that
     * after the call the user is not a member of the chat, but will be able to join it. So if the
     * user is a member of the chat they will also be removed from the chat. If you don't want
     * this, use the parameter [onlyIfBanned].
     *
     * @param chatId Unique identifier for the target group or username of the target supergroup or
     * channel (in the format @username).
     * @param userId Unique identifier of the target user.
     * @param onlyIfBanned Do nothing if the user is not banned.
     *
     * @return True on success.
     */
    fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean? = null,
    ): TelegramBotResult<Boolean> = apiClient.unbanChatMember(
        chatId,
        userId,
        onlyIfBanned,
    )

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

    /**
     * Use this method to get up to date information about the chat (current name of the user
     * for one-on-one conversations, current username of a user, group or channel, etc.).
     *
     * @param chatId Unique identifier for the target chat or username of the target supergroup
     * or channel (in the format @channelusername).
     *
     * @return a Chat object on success.
     */
    fun getChat(chatId: ChatId): TelegramBotResult<Chat> = apiClient.getChat(chatId)

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
     * Use this method to log out from the cloud Bot API server before launching the bot locally. You must log out the bot
     * before running it locally, otherwise there is no guarantee that the bot will receive updates.
     * After a successful call, you can immediately log in on a local server,
     * but will not be able to log in back to the cloud Bot API server for 10 minutes.
     *
     * @return True on success
     */

    fun logOut() = apiClient.logOut().call()

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
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup?
    ) = apiClient.sendSticker(
        chatId,
        sticker,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    ).call()

    fun sendSticker(
        chatId: ChatId,
        sticker: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup?
    ) = apiClient.sendSticker(
        chatId,
        sticker,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
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
        allowSendingWithoutReply: Boolean? = null,
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
        disableNotification = disableNotification,
        replyToMessageId = replyToMessageId,
        allowSendingWithoutReply = allowSendingWithoutReply,
        replyMarkup = replyMarkup
    ).call()

    /**
     * If you sent an invoice requesting a shipping address and the parameter is_flexible was
     * specified, the Bot API will send an [Update] with a [shippingQueryId] field to the bot.
     * Use this method to reply to shipping queries.
     *
     * @param shippingQueryId Unique identifier for the query to be answered.
     * @param ok Specify True if delivery to the specified address is possible and False if there
     * are any problems (for example, if delivery to the specified address is not possible).
     * @param shippingOptions Required if [ok] is True. A list of available shipping options.
     * @param errorMessage Required if [ok] is False. Error message in human readable form that
     * explains why it is impossible to complete the order (e.g. "Sorry, delivery to your desired
     * address is unavailable'). Telegram will display this message to the user.
     *
     * @return True on success.
     */
    fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>? = null,
        errorMessage: String? = null
    ): TelegramBotResult<Boolean> = apiClient.answerShippingQuery(
        shippingQueryId,
        ok,
        shippingOptions,
        errorMessage
    )

    /**
     * Once the user has confirmed their payment and shipping details, the Bot API sends the final
     * confirmation in the form of an [Update] with the field [preCheckoutQueryId]. Use this method to
     * respond to such pre-checkout queries. Note: The Bot API must receive an answer within 10
     * seconds after the pre-checkout query was sent.
     *
     * @param preCheckoutQueryId Unique identifier for the query to be answered.
     * @param ok Specify True if everything is alright (goods are available, etc.) and the bot is
     * ready to proceed with the order. Use False if there are any problems.
     * @param errorMessage Required if ok is False. Error message in human readable form that
     * explains the reason for failure to proceed with the checkout (e.g. "Sorry, somebody just
     * bought the last of our amazing black T-shirts while you were busy filling out your payment
     * details. Please choose a different color or garment!"). Telegram will display this message
     * to the user.
     *
     * @return True on success.
     */
    fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String? = null
    ): TelegramBotResult<Boolean> = apiClient.answerPreCheckoutQuery(
        preCheckoutQueryId,
        ok,
        errorMessage
    )

    /**
     * Use this method to send answers to an inline query. No more than 50 results per query are allowed.
     *
     * @param inlineQueryId Unique identifier for the answered query.
     * @param inlineQueryResults A list of results for the inline query.
     * @param cacheTime The maximum amount of time in seconds that the result of the inline query may be
     * cached on the server. Defaults to 300.
     * @param isPersonal Pass True, if results may be cached on the server side only for the user that sent
     * the query. By default, results may be returned to any user who sends the same query.
     * @param nextOffset Pass the offset that a client should send in the next query with the same text to
     * receive more results. Pass an empty string if there are no more results or if you don't support
     * pagination. Offset length can't exceed 64 bytes.
     * @param switchPmText If passed, clients will display a button with specified text that switches the
     * user to a private chat with the bot and sends the bot a start message with the parameter [switchPmParameter].
     * @param switchPmParameter Deep-linking parameter for the /start message sent to the bot when user
     * presses the switch button. 1-64 characters, only A-Z, a-z, 0-9, _ and - are allowed.
     * Example: An inline bot that sends YouTube videos can ask the user to connect the bot to their YouTube
     * account to adapt search results accordingly. To do this, it displays a 'Connect your YouTube account'
     * button above the results, or even before showing any. The user presses the button, switches to a
     * private chat with the bot and, in doing so, passes a start parameter that instructs the bot to
     * return an oauth link. Once done, the bot can offer a switch_inline button so that the user can
     * easily return to the chat where they wanted to use the bot's inline capabilities.
     *
     * @return True on success.
     */
    fun answerInlineQuery(
        inlineQueryId: String,
        vararg inlineQueryResults: InlineQueryResult,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null
    ): TelegramBotResult<Boolean> = answerInlineQuery(
        inlineQueryId,
        inlineQueryResults.toList(),
        cacheTime,
        isPersonal,
        nextOffset,
        switchPmText,
        switchPmParameter
    )

    /**
     * Use this method to send answers to an inline query. No more than 50 results per query are allowed.
     *
     * @param inlineQueryId Unique identifier for the answered query.
     * @param inlineQueryResults A list of results for the inline query.
     * @param cacheTime The maximum amount of time in seconds that the result of the inline query may be
     * cached on the server. Defaults to 300.
     * @param isPersonal Pass True, if results may be cached on the server side only for the user that sent
     * the query. By default, results may be returned to any user who sends the same query.
     * @param nextOffset Pass the offset that a client should send in the next query with the same text to
     * receive more results. Pass an empty string if there are no more results or if you don't support
     * pagination. Offset length can't exceed 64 bytes.
     * @param switchPmText If passed, clients will display a button with specified text that switches the
     * user to a private chat with the bot and sends the bot a start message with the parameter [switchPmParameter].
     * @param switchPmParameter Deep-linking parameter for the /start message sent to the bot when user
     * presses the switch button. 1-64 characters, only A-Z, a-z, 0-9, _ and - are allowed.
     * Example: An inline bot that sends YouTube videos can ask the user to connect the bot to their YouTube
     * account to adapt search results accordingly. To do this, it displays a 'Connect your YouTube account'
     * button above the results, or even before showing any. The user presses the button, switches to a
     * private chat with the bot and, in doing so, passes a start parameter that instructs the bot to
     * return an oauth link. Once done, the bot can offer a switch_inline button so that the user can
     * easily return to the chat where they wanted to use the bot's inline capabilities.
     *
     * @return True on success.
     */
    fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null
    ): TelegramBotResult<Boolean> = apiClient.answerInlineQuery(
        inlineQueryId,
        inlineQueryResults,
        cacheTime,
        isPersonal,
        nextOffset,
        switchPmText,
        switchPmParameter
    )

    /**
     * Use this method to get the current list of the bot's commands.
     *
     * @return A list of [BotCommand] on success.
     */
    fun getMyCommands(): TelegramBotResult<List<BotCommand>> = apiClient.getMyCommands()

    /**
     * Use this method to change the list of the bot's commands.
     *
     * @param commands A JSON-serialized list of bot commands to be set as the list of the bot's
     * commands. At most 100 commands can be specified.
     *
     * @return True on success.
     */
    fun setMyCommands(
        commands: List<BotCommand>
    ): TelegramBotResult<Boolean> = apiClient.setMyCommands(commands)

    /**
     * Use this method to send a dice, which will have a random value from 1 to 6.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername).
     * @param emoji Emoji on which the dice throw animation is based. Currently, must be one of 🎲, 🎯, 🏀, ⚽, 🎰 or 🎳.
     * Defaults to 🎲.
     * @param disableNotification Sends the message silently. Users will receive a notification with no sound.
     * @param replyToMessageId If the message is a reply, ID of the original message.
     * @param replyMarkup A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to remove
     * reply keyboard or to force a reply from the user.
     *
     * @return the sent Message.
     */
    fun sendDice(
        chatId: ChatId,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null
    ): TelegramBotResult<Message> = apiClient.sendDice(
        chatId,
        emoji,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )

    /**
     * Use this method to set a custom title for an administrator in a supergroup promoted by the bot.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername).
     * @param userId Unique identifier of the target user.
     * @param customTitle New custom title for the administrator; 0-16 characters, emoji are not allowed.
     *
     * @return true on success.
     */
    fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String
    ): TelegramBotResult<Boolean> = apiClient.setChatAdministratorCustomTitle(
        chatId,
        userId,
        customTitle
    )
}
