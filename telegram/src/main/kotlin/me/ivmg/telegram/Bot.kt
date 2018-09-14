package me.ivmg.telegram

import me.ivmg.telegram.dispatcher.Dispatcher
import me.ivmg.telegram.entities.ChatAction
import me.ivmg.telegram.entities.InlineKeyboardMarkup
import me.ivmg.telegram.entities.InputMedia
import me.ivmg.telegram.entities.ParseMode
import me.ivmg.telegram.entities.ReplyMarkup
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.payment.PaymentInvoiceInfo
import me.ivmg.telegram.entities.payment.ShippingOption
import me.ivmg.telegram.errors.RetrieveUpdatesError
import me.ivmg.telegram.errors.TelegramError
import me.ivmg.telegram.network.ApiClient
import me.ivmg.telegram.network.call
import me.ivmg.telegram.types.DispatchableObject
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Date
import java.io.File as SystemFile

fun bot(body: Bot.Builder.() -> Unit) = Bot.Builder().build(body)

fun Bot.Builder.dispatch(body: Dispatcher.() -> Unit): Dispatcher {
    updater.dispatcher.body()
    return updater.dispatcher
}

class Bot private constructor(
    private val updater: Updater,
    token: String,
    apiUrl: String,
    timeout: Int = 30,
    logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
) {
    private val apiClient: ApiClient = ApiClient(token, apiUrl, timeout, logLevel)

    init {
        updater.bot = this
        updater.dispatcher.bot = this
    }

    class Builder {
        lateinit var token: String
        var timeout: Int = 30
        var apiUrl: String = "https://api.telegram.org/bot"
        var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY

        val updater = Updater()

        fun build(): Bot {
            return Bot(updater, token, apiUrl, timeout, logLevel)
        }

        fun build(body: Bot.Builder.() -> Unit): Bot {
            body()
            return Bot(updater, token, apiUrl, timeout, logLevel)
        }
    }

    fun startPolling() = updater.startPolling()

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
            false -> {
                val errorMessage: String = when {
                    error?.message != null -> error.message!!
                    response.errorBody() != null -> response.errorBody().toString()
                    else -> "There was a problem retrieving updates from Telegram server"
                }

                return arrayListOf(RetrieveUpdatesError(errorMessage) as TelegramError)
            }
        }

        return emptyList()
    }

    fun processUpdate(update: Update) {
        updater.dispatcher.updatesQueue.put(update)
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
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        disableNotification,
        replyToMessageId
    ).call()

    fun sendPhoto(
        chatId: Long,
        photo: String,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        disableNotification,
        replyToMessageId
    ).call()

    fun sendAudio(
        chatId: Long,
        photo: String,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendPhoto(
        chatId,
        photo,
        caption,
        disableNotification,
        replyToMessageId
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
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        document,
        caption,
        disableNotification,
        replyToMessageId,
        replyMarkup
    ).call()

    fun sendDocument(
        chatId: Long,
        fileId: String,
        caption: String? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendDocument(
        chatId,
        fileId,
        caption,
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
        audio: SystemFile,
        duration: Int? = null,
        length: Int? = null,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        replyMarkup: ReplyMarkup? = null
    ) = apiClient.sendVideoNote(
        chatId,
        audio,
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
        chatId: Long,
        media: List<InputMedia>,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ) = apiClient.sendMediaGroup(
        chatId,
        media,
        disableNotification,
        replyToMessageId
    ).call()

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
        chatId: Long?,
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

    fun kickChatMember(chatId: Long, userId: Long, untilDate: Date) =
        apiClient.kickChatMember(chatId, userId, untilDate).call()

    fun unbanChatMember(chatId: Long, userId: Long) = apiClient.unbanChatMember(chatId, userId).call()

    fun restrictChatMember(
        chatId: Long,
        userId: Long,
        untilDate: Date? = null,
        canSendMessages: Boolean? = null,
        canSendMediaMessages: Boolean? = null,
        canSendOtherMessages: Boolean? = null,
        canAddWebPagePreviews: Boolean? = null
    ) = apiClient.restrictChatMember(
        chatId,
        userId,
        untilDate,
        canSendMessages,
        canSendMediaMessages,
        canSendOtherMessages,
        canAddWebPagePreviews
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

    fun exportChatInviteLink(chatId: Long) = apiClient.exportChatInviteLink(chatId).call()

    fun setChatPhoto(
        chatId: Long,
        photo: SystemFile
    ) {

        // TODO
        // val inputFile = InputFile(chatId = chatId, photo = photo)
        // return service.setChatPhoto(chatId, )
    }

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

    /***
     * Stickers
     */

    // TODO sticker methods
}