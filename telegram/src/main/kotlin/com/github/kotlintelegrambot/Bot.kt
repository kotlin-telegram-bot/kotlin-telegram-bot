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
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.UserProfilePhotos
import com.github.kotlintelegrambot.entities.WebhookInfo
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.files.File
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.entities.payments.PaymentInvoiceInfo
import com.github.kotlintelegrambot.entities.payments.ShippingOption
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.polls.PollType
import com.github.kotlintelegrambot.entities.stickers.MaskPosition
import com.github.kotlintelegrambot.entities.stickers.StickerSet
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.network.CallResponse
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.network.bimap
import com.github.kotlintelegrambot.network.call
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import com.github.kotlintelegrambot.updater.SuspendLooper
import com.github.kotlintelegrambot.updater.Updater
import com.github.kotlintelegrambot.webhook.WebhookConfig
import com.github.kotlintelegrambot.webhook.WebhookConfigBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.net.Proxy
import java.io.File as SystemFile

public fun bot(body: Bot.Builder.() -> Unit): Bot = Bot.Builder().build(body)

public fun Bot.Builder.dispatch(body: Dispatcher.() -> Unit) {
    dispatcherConfiguration = body
}

public fun Bot.Builder.webhook(
    body: WebhookConfigBuilder.() -> Unit,
) {
    val webhookConfigBuilder = WebhookConfigBuilder()
    webhookConfigBuilder.apply(body)
    webhookConfig = webhookConfigBuilder.build()
}

public class Bot private constructor(
    private val updater: Updater,
    private val dispatcher: Dispatcher,
    private val updatesChannel: Channel<DispatchableObject>,
    private val updateMapper: UpdateMapper,
    private val webhookConfig: WebhookConfig?,
    private val apiClient: ApiClient,
) {

    init {
        dispatcher.bot = this
    }

    public class Builder {
        private val gson = GsonFactory.createForApiClient()
        private val updateMapper = UpdateMapper(gson)
        public var webhookConfig: WebhookConfig? = null
        public lateinit var token: String
        public var timeout: Int = 30
        public var apiUrl: String = "https://api.telegram.org/"
        public var logLevel: LogLevel = LogLevel.None
        public var proxy: Proxy = Proxy.NO_PROXY
        internal var dispatcherConfiguration: Dispatcher.() -> Unit = { }
        private val coroutineDispatcher = Dispatchers.IO

        public fun build(): Bot {
            val updatesQueue = Channel<DispatchableObject>()
            val looper = SuspendLooper(coroutineDispatcher)
            val apiClient = ApiClient(token, apiUrl, timeout, logLevel, proxy, gson)
            val updater = Updater(looper, updatesQueue, apiClient, timeout)
            val dispatcher = Dispatcher(
                updatesQueue,
                coroutineDispatcher,
                logLevel,
            ).apply(dispatcherConfiguration)

            return Bot(
                updater,
                dispatcher,
                updatesQueue,
                updateMapper,
                webhookConfig,
                apiClient,
            )
        }

        public fun build(body: Builder.() -> Unit): Bot {
            body()
            return build()
        }
    }

    public fun startPolling(wait: Boolean) {
        dispatcher.launchCheckingUpdates()
        updater.launchPolling()
        if (wait) {
            runBlocking {
                dispatcher.awaitCancellation()
                updater.awaitCancellation()
            }
        }
    }

    public fun stopPolling() {
        updater.cancelPolling()
        dispatcher.cancelCheckingUpdates()
    }

    /**
     * Starts a webhook through the setWebhook Telegram's API operation and starts checking
     * updates if it was successfully set.
     * @return true if the webhook was successfully set or false otherwise
     */
    public fun startWebhook(): Boolean {
        if (webhookConfig == null) {
            error("To start a webhook you need to configure it on bot set up. Check the `webhook` builder function")
        }

        val setWebhookResult = runBlocking {
            setWebhook(
                webhookConfig.url,
                webhookConfig.certificate,
                webhookConfig.ipAddress,
                webhookConfig.maxConnections,
                webhookConfig.allowedUpdates
            )
        }
        val webhookSet = setWebhookResult.bimap(
            mapResponse = { true },
            mapError = { false }
        )

        if (webhookSet) {
            dispatcher.launchCheckingUpdates()
        }

        return webhookSet
    }

    /**
     * Deletes a webhook through the deleteWebhook Telegram's API operation and stops checking updates.
     * @return true if the webhook was successfully deleted or false otherwise
     */
    public suspend fun stopWebhook(): Boolean {
        if (webhookConfig == null) {
            error("To stop a webhook you need to configure it on bot set up. Check the `webhook` builder function")
        }

        dispatcher.cancelCheckingUpdates()

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
     * queue. All previous updates will be forgotten.
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
    public suspend fun getUpdates(
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

    public suspend fun setWebhook(
        url: String,
        certificate: TelegramFile? = null,
        ipAddress: String? = null,
        maxConnections: Int? = null,
        allowedUpdates: List<String>? = null,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.setWebhook(url, certificate, ipAddress, maxConnections, allowedUpdates)
    }

    @Suppress("UNCHECKED_CAST")
    public suspend fun deleteWebhook(): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.deleteWebhook() }

    public suspend fun getWebhookInfo(): Pair<CallResponse<Response<WebhookInfo>?>?, Exception?> =
        call { apiClient.getWebhookInfo() }

    public suspend fun processUpdate(update: Update) {
        updatesChannel.send(update)
    }

    public suspend fun processUpdate(updateJson: String) {
        val update = updateMapper.jsonToUpdate(updateJson)
        processUpdate(update)
    }

    /**
     * A simple method for testing your bot's authentication token.
     *
     * @return basic information about the bot in form of a [User] object.
     */
    public suspend fun getMe(): TelegramBotResult<User> = apiClient.getMe()

    /**
     * Use this method to send text messages.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     * @param text text of the message to be sent, 1-4096 characters after entities parsing.
     * @param parseMode mode for parsing entities in the message text.
     * @param disableWebPagePreview disables link previews for links in this message.
     * @param disableNotification sends the message silently - users will receive a notification
     * with no sound.
     * @param replyToMessageId if the message is a reply, ID of the original message.
     * @param replyMarkup additional options - inline keyboard, custom reply keyboard,
     * instructions to remove reply keyboard or to force a reply from the user.
     *
     * @return the sent [Message] on success.
     */
    public suspend fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): TelegramBotResult<Message> = apiClient.sendMessage(
        chatId,
        text,
        parseMode,
        disableWebPagePreview,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )

    /**
     * Use this method to forward messages of any kind. Service messages can't be forwarded.
     *
     * @return the sent [Message] on success
     */
    public suspend fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        disableNotification: Boolean? = null,
    ): TelegramBotResult<Message> = apiClient.forwardMessage(
        chatId,
        fromChatId,
        messageId,
        disableNotification
    )

    public suspend fun copyMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Long,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<MessageId>?>?, Exception?> = call {
        apiClient.copyMessage(
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
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendPhoto(chatId, TelegramFile.ByFile(photo), caption, parseMode, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendPhoto(
        chatId: ChatId,
        photo: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendPhoto(
            chatId,
            TelegramFile.ByFile(photo),
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendPhoto(chatId, TelegramFile.ByFileId(photo), caption, parseMode, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendPhoto(
        chatId: ChatId,
        photo: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendPhoto(
            chatId,
            TelegramFile.ByFileId(photo),
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendAudio(chatId, TelegramFile.ByFile(audio), duration, performer, title, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendAudio(
        chatId: ChatId,
        audio: SystemFile,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAudio(
            chatId,
            TelegramFile.ByFile(audio),
            duration,
            performer,
            title,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendAudio(chatId, TelegramFile.ByFileId(audio), duration, performer, title, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendAudio(
        chatId: ChatId,
        audio: String,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAudio(
            chatId,
            TelegramFile.ByFileId(audio),
            duration,
            performer,
            title,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendDocument(chatId, TelegramFile.ByFile(document), caption, parseMode, disableContentTypeDetection, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendDocument(
        chatId: ChatId,
        document: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendDocument(
            chatId,
            TelegramFile.ByFile(document),
            caption,
            parseMode,
            disableContentTypeDetection,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendDocument(chatId, TelegramFile.ByByteArray(fileBytes, fileName), caption, parseMode, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup, mimeType)")
    )
    public suspend fun sendDocument(
        chatId: ChatId,
        fileBytes: ByteArray,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        fileName: String,
        mimeType: String? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendDocument(
            chatId,
            TelegramFile.ByByteArray(fileBytes, fileName),
            caption,
            parseMode,
            disableContentTypeDetection,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup,
            mimeType
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendDocument(chatId, TelegramFile.ByFileId(fileId), caption, parseMode, disableContentTypeDetection, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendDocument(
        chatId: ChatId,
        fileId: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendDocument(
            chatId,
            TelegramFile.ByFileId(fileId),
            caption,
            parseMode,
            disableContentTypeDetection,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVideo(chatId, TelegramFile.ByFile(video), duration, width, height, caption, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVideo(
        chatId: ChatId,
        video: SystemFile,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideo(
            chatId,
            TelegramFile.ByFile(video),
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

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVideo(chatId, TelegramFile.ByFileId(fileId), duration, width, height, caption, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVideo(
        chatId: ChatId,
        fileId: String,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideo(
            chatId,
            TelegramFile.ByFileId(fileId),
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

    public suspend fun sendPhoto(
        chatId: ChatId,
        photo: TelegramFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendPhoto(
            chatId,
            photo,
            caption,
            parseMode,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendAudio(
        chatId: ChatId,
        audio: TelegramFile,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAudio(
            chatId,
            audio,
            duration,
            performer,
            title,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendDocument(
        chatId: ChatId,
        document: TelegramFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        mimeType: String? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendDocument(
            chatId,
            document,
            caption,
            parseMode,
            disableContentTypeDetection,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup,
            mimeType
        )
    }

    public suspend fun sendVideo(
        chatId: ChatId,
        video: TelegramFile,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideo(
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
        )
    }

    /**
     * Use this method to send a game. On success, the sent Message is returned.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel
     * (in the format @channelusername).
     * @param gameShortName Short name of the game, serves as the unique identifier for the game.
     * @param disableNotification Sends the message silently. Users will receive a notification with no sound.
     * @param replyToMessageId If the message is a reply, ID of the original message.
     * @param allowSendingWithoutReply Pass True, if the message should be sent even if the specified
     * replied-to message is not found
     * @param replyMarkup A JSON-serialized object for an inline keyboard. If empty, one 'Play game_title'
     * button will be shown. If not empty, the first button must launch the game.
     *
     * @return the sent Message.
     */
    public suspend fun sendGame(
        chatId: ChatId,
        gameShortName: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): TelegramBotResult<Message> = apiClient.sendGame(
        chatId,
        gameShortName,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )

    @Suppress("DEPRECATION")
    @Deprecated("Use overloaded version instead")
    public suspend fun sendAnimation(
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
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAnimation(
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
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendAnimation(chatId, TelegramFile.ByFileId(fileId), duration, width, height, caption, parseMode, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendAnimation(
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
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAnimation(
            chatId,
            TelegramFile.ByFileId(fileId),
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

    public suspend fun sendAnimation(
        chatId: ChatId,
        animation: TelegramFile,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendAnimation(
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
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVoice(chatId, TelegramFile.ByByteArray(audio), caption, parseMode, captionEntities, duration, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVoice(
        chatId: ChatId,
        audio: ByteArray,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVoice(
            chatId,
            TelegramFile.ByByteArray(audio),
            caption,
            parseMode,
            captionEntities,
            duration,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVoice(chatId, TelegramFile.ByFile(audio), caption, parseMode, captionEntities, duration, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVoice(
        chatId: ChatId,
        audio: SystemFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVoice(
            chatId,
            TelegramFile.ByFile(audio),
            caption,
            parseMode,
            captionEntities,
            duration,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVoice(chatId, TelegramFile.ByFileId(audioId), caption, parseMode, captionEntities, duration, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVoice(
        chatId: ChatId,
        audioId: String,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVoice(
            chatId,
            TelegramFile.ByFileId(audioId),
            caption,
            parseMode,
            captionEntities,
            duration,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendVoice(
        chatId: ChatId,
        audio: TelegramFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVoice(
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
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVideoNote(chatId, TelegramFile.ByFile(videoNote), duration, length, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: SystemFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideoNote(
            chatId,
            TelegramFile.ByFile(videoNote),
            duration,
            length,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    @Deprecated(
        "Use overloaded version instead",
        ReplaceWith("sendVideoNote(chatId, TelegramFile.ByFileId(videoNoteId), duration, length, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)")
    )
    public suspend fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: String,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideoNote(
            chatId,
            TelegramFile.ByFileId(videoNoteId),
            duration,
            length,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: TelegramFile.ByFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideoNote(
            chatId,
            videoNote,
            duration,
            length,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendVideoNote(
        chatId: ChatId,
        videoNoteId: TelegramFile.ByFileId,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVideoNote(
            chatId,
            videoNoteId,
            duration,
            length,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    /**
     * Use this method to send a group of photos, videos, documents or audios as an album.
     * Documents and audio files can be only grouped on an album with messages of the same type.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername).
     * @param mediaGroup An object describing photos and videos to be sent, must include 2-10 items.
     * @param disableNotification Sends the messages silently. Users will receive a notification with no sound.
     * @param replyToMessageId If the messages are a reply, ID of the original message.
     *
     * @return a list of the sent Messages.
     */
    public suspend fun sendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
    ): TelegramBotResult<List<Message>> = apiClient.sendMediaGroup(
        chatId,
        mediaGroup,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply
    )

    public suspend fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        livePeriod: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendLocation(
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

    /**
     * Use this method to send a native poll.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     * @param question Poll question, 1-300 characters.
     * @param options A JSON-serialized list of answer options, 2-10 strings 1-100 characters each.
     * @param isAnonymous True, if the poll needs to be anonymous, defaults to True.
     * @param type Poll type, “quiz” or “regular”, defaults to “regular”.
     * @param allowsMultipleAnswers True, if the poll allows multiple answers, ignored for polls
     * in quiz mode, defaults to False.
     * @param correctOptionId 0-based identifier of the correct answer option, required for polls
     * in quiz mode.
     * @param explanation Text that is shown when a user chooses an incorrect answer or taps on the
     * lamp icon in a quiz-style poll, 0-200 characters with at most 2 line feeds after entities
     * parsing.
     * @param explanationParseMode Mode for parsing entities in the explanation. See formatting
     * options for more details.
     * @param openPeriod Amount of time in seconds the poll will be active after creation, 5-600.
     * Can't be used together with [closeDate].
     * @param closeDate Point in time (Unix timestamp) when the poll will be automatically closed.
     * Must be at least 5 and no more than 600 seconds in the future. Can't be used together
     * with [openPeriod].
     * @param isClosed Pass True, if the poll needs to be immediately closed. This can be useful
     * for poll preview.
     * @param disableNotification Sends the message silently. Users will receive a notification
     * with no sound.
     * @param replyToMessageId If the message is a reply, ID of the original message.
     * @param allowSendingWithoutReply Pass True, if the message should be sent even if the
     * specified replied-to message is not found.
     * @param replyMarkup Additional interface options. A JSON-serialized object for an inline
     * keyboard, custom reply keyboard, instructions to remove reply keyboard or to force a reply
     * from the user.
     *
     * @return On success, the sent [Message] is returned.
     */
    public suspend fun sendPoll(
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
    ): TelegramBotResult<Message> = apiClient.sendPoll(
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
        replyMarkup,
    )

    public suspend fun editMessageLiveLocation(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        latitude: Float,
        longitude: Float,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.editMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            latitude,
            longitude,
            replyMarkup
        )
    }

    public suspend fun stopMessageLiveLocation(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.stopMessageLiveLocation(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    public suspend fun sendVenue(
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
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendVenue(
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

    public suspend fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendContact(
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

    public suspend fun sendChatAction(
        chatId: ChatId,
        action: ChatAction,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.sendChatAction(chatId, action) }

    public suspend fun getUserProfilePhotos(
        userId: Long,
        offset: Long? = null,
        limit: Int? = null,
    ): Pair<CallResponse<Response<UserProfilePhotos>?>?, Exception?> =
        call { apiClient.getUserProfilePhotos(userId, offset, limit) }

    public suspend fun getFile(fileId: String): Pair<CallResponse<Response<File>?>?, Exception?> =
        call { apiClient.getFile(fileId) }

    public suspend fun downloadFile(filePath: String): Pair<CallResponse<ResponseBody?>?, Exception?> =
        call { apiClient.downloadFile(filePath) }

    public suspend fun downloadFileBytes(fileId: String): ByteArray? {
        val fileResp = getFile(fileId).first
        return if (fileResp?.isSuccessful == true) {
            val filePath = fileResp.body()?.result?.filePath
            if (filePath == null) null else downloadFile(filePath).first?.body()?.bytes()
        } else {
            null
        }
    }

    public suspend fun banChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Long? = null, // unix time - https://en.wikipedia.org/wiki/Unix_time
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.banChatMember(chatId, userId, untilDate) }

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
    public suspend fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean? = null,
    ): TelegramBotResult<Boolean> = apiClient.unbanChatMember(
        chatId,
        userId,
        onlyIfBanned,
    )

    public suspend fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        chatPermissions: ChatPermissions,
        untilDate: Long? = null, // unix time - https://en.wikipedia.org/wiki/Unix_time
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.restrictChatMember(
            chatId,
            userId,
            chatPermissions,
            untilDate
        )
    }

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
    public suspend fun promoteChatMember(
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
        canPromoteMembers: Boolean? = null,
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

    public suspend fun setChatPermissions(
        chatId: ChatId,
        permissions: ChatPermissions,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.setChatPermissions(chatId, permissions) }

    public suspend fun exportChatInviteLink(chatId: ChatId): Pair<CallResponse<Response<String>?>?, Exception?> =
        call { apiClient.exportChatInviteLink(chatId) }

    public suspend fun setChatPhoto(
        chatId: ChatId,
        photo: SystemFile,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.setChatPhoto(chatId, photo) }

    public suspend fun deleteChatPhoto(chatId: ChatId): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.deleteChatPhoto(chatId) }

    public suspend fun setChatTitle(
        chatId: ChatId,
        title: String,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.setChatTitle(chatId, title) }

    public suspend fun setChatDescription(
        chatId: ChatId,
        description: String,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.setChatDescription(chatId, description) }

    public suspend fun pinChatMessage(
        chatId: ChatId,
        messageId: Long,
        disableNotification: Boolean? = null,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.pinChatMessage(chatId, messageId, disableNotification) }

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
    public suspend fun unpinChatMessage(
        chatId: ChatId,
        messageId: Long? = null,
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
    public suspend fun unpinAllChatMessages(
        chatId: ChatId,
    ): TelegramBotResult<Boolean> = apiClient.unpinAllChatMessages(chatId)

    public suspend fun leaveChat(chatId: ChatId): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.leaveChat(chatId) }

    /**
     * Use this method to get up-to-date information about the chat (current name of the user
     * for one-on-one conversations, current username of a user, group or channel, etc.).
     *
     * @param chatId Unique identifier for the target chat or username of the target supergroup
     * or channel (in the format @channelusername).
     *
     * @return a Chat object on success.
     */
    public suspend fun getChat(chatId: ChatId): TelegramBotResult<Chat> = apiClient.getChat(chatId)

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
    public suspend fun getChatAdministrators(
        chatId: ChatId,
    ): TelegramBotResult<List<ChatMember>> = apiClient.getChatAdministrators(chatId)

    public suspend fun getChatMemberCount(chatId: ChatId): Pair<CallResponse<Response<Int>?>?, Exception?> =
        call { apiClient.getChatMemberCount(chatId) }

    /**
     * Use this method to get information about a member of a chat.
     *
     * @param chatId Unique identifier for the target chat or username of the target supergroup or
     * channel (in the format @channelusername).
     * @param userId Unique identifier of the target user.
     *
     * @return A [ChatMember] object on success.
     */
    public suspend fun getChatMember(
        chatId: ChatId,
        userId: Long,
    ): TelegramBotResult<ChatMember> = apiClient.getChatMember(
        chatId,
        userId,
    )

    /**
     * Use this method to set a new group sticker set for a supergroup. The bot must be an
     * administrator in the chat for this to work and must have the appropriate admin rights. Use
     * the field [canSetStickerSet] optionally returned in [getChat] requests to check if the bot
     * can use this method.
     *
     * @param chatId Unique identifier for the target chat or username of the target supergroup (in
     * the format @supergroupusername).
     * @param stickerSetName Name of the sticker set to be set as the group sticker set.
     *
     * @return True on success.
     */
    public suspend fun setChatStickerSet(
        chatId: ChatId,
        stickerSetName: String,
    ): TelegramBotResult<Boolean> = apiClient.setChatStickerSet(
        chatId,
        stickerSetName,
    )

    /**
     * Use this method to delete a group sticker set from a supergroup. The bot must be an
     * administrator in the chat for this to work and must have the appropriate admin rights. Use
     * the field [canSetStickerSet] optionally returned in [getChat] requests to check if the bot
     * can use this method.
     *
     * @param chatId Unique identifier for the target chat or username of the target supergroup (in
     * the format @supergroupusername).
     *
     * @return True on success.
     */
    public suspend fun deleteChatStickerSet(
        chatId: ChatId,
    ): TelegramBotResult<Boolean> = apiClient.deleteChatStickerSet(chatId)

    /**
     * Use this method to send answers to callback queries sent from inline keyboards. The answer
     * will be displayed to the user as a notification at the top of the chat screen or as an
     * alert.
     *
     * @param callbackQueryId Unique identifier for the query to be answered.
     * @param text Text of the notification. If not specified, nothing will be shown to the user,
     * 0-200 characters.
     * @param showAlert If true, an alert will be shown by the client instead of a notification at
     * the top of the chat screen. Defaults to false.
     * @param url URL that will be opened by the user's client. If you have created a Game and
     * accepted the conditions via @Botfather, specify the URL that opens your game — note that
     * this will only work if the query comes from a callback game button.
     * @param cacheTime The maximum amount of time in seconds that the result of the callback
     * query may be cached client-side. Telegram apps will support caching starting in version
     * 3.14. Defaults to 0.
     *
     * @return True on success.
     */
    public suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean? = null,
        url: String? = null,
        cacheTime: Int? = null,
    ): TelegramBotResult<Boolean> = apiClient.answerCallbackQuery(
        callbackQueryId,
        text,
        showAlert,
        url,
        cacheTime
    )

    /**
     * Use this method to log out from the cloud Bot API server before launching the bot locally. You must log out the bot
     * before running it locally, otherwise there is no guarantee that the bot will receive updates.
     * After a successful call, you can immediately log in on a local server,
     * but will not be able to log in back to the cloud Bot API server for 10 minutes.
     *
     * @return True on success
     */

    public suspend fun logOut(): Pair<CallResponse<Response<Boolean>?>?, Exception?> =
        call { apiClient.logOut() }

    /**
     * Updating messages
     */

    public suspend fun editMessageText(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.editMessageText(
            chatId,
            messageId,
            inlineMessageId,
            text,
            parseMode,
            disableWebPagePreview,
            replyMarkup
        )
    }

    public suspend fun editMessageCaption(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        caption: String,
        parseMode: ParseMode? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.editMessageCaption(
            chatId,
            messageId,
            inlineMessageId,
            caption,
            parseMode,
            replyMarkup
        )
    }

    public suspend fun editMessageMedia(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        media: InputMedia,
        replyMarkup: ReplyMarkup?,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.editMessageMedia(
            chatId,
            messageId,
            inlineMessageId,
            media,
            replyMarkup
        )
    }

    public suspend fun editMessageReplyMarkup(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.editMessageReplyMarkup(
            chatId,
            messageId,
            inlineMessageId,
            replyMarkup
        )
    }

    /**
     * Use this method to stop a poll which was sent by the bot.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername).
     * @param messageId Identifier of the original message with the poll.
     * @param replyMarkup A JSON-serialized object for a new message inline keyboard.
     *
     * @return On success, the stopped [Poll] with the final results.
     */
    public suspend fun stopPoll(
        chatId: ChatId,
        messageId: Long,
        replyMarkup: InlineKeyboardMarkup? = null,
    ): TelegramBotResult<Poll> = apiClient.stopPoll(
        chatId,
        messageId,
        replyMarkup,
    )

    /**
     * Use this method to delete a message, including service messages, with the following limitations:
     * - A message can only be deleted if it was sent less than 48 hours ago.
     * - A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.
     * - Bots can delete outgoing messages in private chats, groups, and supergroups.
     * - Bots can delete incoming messages in private chats.
     * - Bots granted `can_post_messages` permissions can delete outgoing messages in channels.
     * - If the bot is an administrator of a group, it can delete any message there.
     * - If the bot has `can_delete_messages` permission in a supergroup or a channel, it can delete any message there.
     *
     * @param chatId Unique identifier for the target chat or username of the target channel (in
     * the format @channelusername)
     * @param messageId Identifier of the message to delete.
     *
     * @return True on success.
     */
    public suspend fun deleteMessage(
        chatId: ChatId,
        messageId: Long,
    ): TelegramBotResult<Boolean> = apiClient.deleteMessage(
        chatId,
        messageId,
    )

    /***
     * Stickers.
     */
    public suspend fun sendSticker(
        chatId: ChatId,
        sticker: SystemFile,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup?,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendSticker(
            chatId,
            sticker,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun sendSticker(
        chatId: ChatId,
        sticker: String,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup?,
    ): Pair<CallResponse<Response<Message>?>?, Exception?> = call {
        apiClient.sendSticker(
            chatId,
            sticker,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    }

    public suspend fun getStickerSet(
        name: String,
    ): Pair<CallResponse<Response<StickerSet>?>?, Exception?> =
        call { apiClient.getStickerSet(name) }

    public suspend fun uploadStickerFile(
        userId: Long,
        pngSticker: SystemFile,
    ): Pair<CallResponse<Response<File>?>?, Exception?> = call {
        apiClient.uploadStickerFile(
            userId,
            pngSticker
        )
    }

    public suspend fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: SystemFile,
        emojis: String,
        containsMasks: Boolean? = null,
        maskPosition: MaskPosition?,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.createNewStickerSet(
            userId,
            name,
            title,
            pngSticker,
            emojis,
            containsMasks,
            maskPosition
        )
    }

    public suspend fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        pngSticker: String,
        emojis: String,
        containsMasks: Boolean? = null,
        maskPosition: MaskPosition?,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.createNewStickerSet(
            userId,
            name,
            title,
            pngSticker,
            emojis,
            containsMasks,
            maskPosition
        )
    }

    public suspend fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: SystemFile,
        emojis: String,
        maskPosition: MaskPosition?,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.addStickerToSet(
            userId,
            name,
            pngSticker,
            emojis,
            maskPosition
        )
    }

    public suspend fun addStickerToSet(
        userId: Long,
        name: String,
        pngSticker: String,
        emojis: String,
        maskPosition: MaskPosition?,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.addStickerToSet(
            userId,
            name,
            pngSticker,
            emojis,
            maskPosition
        )
    }

    public suspend fun setStickerPositionInSet(
        sticker: String,
        position: Int,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.setStickerPositionInSet(
            sticker,
            position
        )
    }

    public suspend fun deleteStickerFromSet(
        sticker: String,
    ): Pair<CallResponse<Response<Boolean>?>?, Exception?> = call {
        apiClient.deleteStickerFromSet(
            sticker
        )
    }

    /**
     * Use this method to send invoices.
     *
     * @param chatId Unique identifier for the target private chat.
     * @param paymentInvoiceInfo All the payment invoice information.
     * @param disableNotification Sends the message silently. Users will receive a notification
     * with no sound.
     * @param replyToMessageId If the message is a reply, ID of the original message.
     * @param replyMarkup Additional interface options. An inlineKeyboard. If empty, one 'Pay total
     * price' button will be shown. If not empty, the first button must be a Pay button.
     *
     * @return The sent [Message].
     */
    public suspend fun sendInvoice(
        chatId: ChatId,
        paymentInvoiceInfo: PaymentInvoiceInfo,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
    ): TelegramBotResult<Message> = apiClient.sendInvoice(
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
    )

    /**
     * If you sent an invoice requesting a shipping address and the parameter is_flexible was
     * specified, the Bot API will send an [Update] with a [shippingQueryId] field to the bot.
     * Use this method to reply to shipping queries.
     *
     * @param shippingQueryId Unique identifier for the query to be answered.
     * @param ok Specify True if delivery to the specified address is possible and False if there
     * are any problems (for example, if delivery to the specified address is not possible).
     * @param shippingOptions Required if [ok] is True. A list of available shipping options.
     * @param errorMessage Required if [ok] is False. Error message in human-readable form that
     * explains why it is impossible to complete the order (e.g. "Sorry, delivery to your desired
     * address is unavailable"). Telegram will display this message to the user.
     *
     * @return True on success.
     */
    public suspend fun answerShippingQuery(
        shippingQueryId: String,
        ok: Boolean,
        shippingOptions: List<ShippingOption>? = null,
        errorMessage: String? = null,
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
     * @param errorMessage Required if ok is False. Error message in human-readable form that
     * explains the reason for failure to proceed with the checkout (e.g. "Sorry, somebody just
     * bought the last of our amazing black T-shirts while you were busy filling out your payment
     * details. Please choose a different color or garment!"). Telegram will display this message
     * to the user.
     *
     * @return True on success.
     */
    public suspend fun answerPreCheckoutQuery(
        preCheckoutQueryId: String,
        ok: Boolean,
        errorMessage: String? = null,
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
    public suspend fun answerInlineQuery(
        inlineQueryId: String,
        vararg inlineQueryResults: InlineQueryResult,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null,
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
    public suspend fun answerInlineQuery(
        inlineQueryId: String,
        inlineQueryResults: List<InlineQueryResult>,
        cacheTime: Int? = null,
        isPersonal: Boolean = false,
        nextOffset: String? = null,
        switchPmText: String? = null,
        switchPmParameter: String? = null,
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
    public suspend fun getMyCommands(): TelegramBotResult<List<BotCommand>> =
        apiClient.getMyCommands()

    /**
     * Use this method to change the list of the bot's commands.
     *
     * @param commands A JSON-serialized list of bot commands to be set as the list of the bot's
     * commands. At most 100 commands can be specified.
     *
     * @return True on success.
     */
    public suspend fun setMyCommands(
        commands: List<BotCommand>,
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
    public suspend fun sendDice(
        chatId: ChatId,
        emoji: DiceEmoji? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
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
    public suspend fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String,
    ): TelegramBotResult<Boolean> = apiClient.setChatAdministratorCustomTitle(
        chatId,
        userId,
        customTitle
    )
}
