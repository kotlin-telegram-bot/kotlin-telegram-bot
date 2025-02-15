package com.github.kotlintelegrambot.network.multipart

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaDocument
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaVideo
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.network.ApiConstants
import com.github.kotlintelegrambot.network.MediaTypeConstants
import com.github.kotlintelegrambot.network.retrofit.converters.ChatIdConverterFactory
import com.google.gson.Gson
import okhttp3.MultipartBody

internal class MultipartBodyFactory(private val gson: Gson) {

    fun createForSendMediaGroup(
        chatId: ChatId,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean?,
    ): List<MultipartBody.Part> {
        val chatIdString = ChatIdConverterFactory.chatIdToString(chatId)
        val chatIdPart = chatIdString.toMultipartBodyPart(ApiConstants.CHAT_ID)
        return createSendMediaGroupMultipartBody(chatIdPart, mediaGroup, disableNotification, protectContent, replyToMessageId, allowSendingWithoutReply)
    }

    private fun createSendMediaGroupMultipartBody(
        chatIdPart: MultipartBody.Part,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean?,
    ): List<MultipartBody.Part> {
        val filesParts = mediaGroup.takeAsMultipart()
        val mediaGroupPart = gson.toJson(mediaGroup.medias).toMultipartBodyPart(ApiConstants.SendMediaGroup.MEDIA)
        val disableNotificationPart = disableNotification?.toMultipartBodyPart(ApiConstants.DISABLE_NOTIFICATION)
        val protectContentPart = protectContent?.toMultipartBodyPart(ApiConstants.PROTECT_CONTENT)
        val replyToMessageIdPart = replyToMessageId?.toMultipartBodyPart(ApiConstants.REPLY_TO_MESSAGE_ID)
        val allowSendingWithoutReplyPart = allowSendingWithoutReply?.toMultipartBodyPart(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY)

        return listOfNotNull(chatIdPart, mediaGroupPart, disableNotificationPart, protectContentPart, replyToMessageIdPart, allowSendingWithoutReplyPart) + filesParts
    }

    private fun MediaGroup.takeAsMultipart(): List<MultipartBody.Part> = medias.flatMap { groupableMedia ->
        when {
            groupableMedia is InputMediaDocument ->
                telegramFileToMultipart(groupableMedia.media, MediaTypeConstants.DOCUMENT)

            groupableMedia is InputMediaPhoto ->
                telegramFileToMultipart(groupableMedia.media, MediaTypeConstants.IMAGE)

            groupableMedia is InputMediaVideo && groupableMedia.thumb != null ->
                telegramFileToMultipart(groupableMedia.media, MediaTypeConstants.VIDEO) +
                    telegramFileToMultipart(groupableMedia.thumb, MediaTypeConstants.IMAGE)

            groupableMedia is InputMediaVideo ->
                telegramFileToMultipart(groupableMedia.media, MediaTypeConstants.VIDEO)

            else -> emptyList()
        }
    }

    private fun telegramFileToMultipart(tFile: TelegramFile, mediaType: String): List<MultipartBody.Part> = when (tFile) {
        is TelegramFile.ByFile -> listOf(tFile.file.toMultipartBodyPart(mediaType = mediaType))
        is TelegramFile.ByByteArray -> {
            if (tFile.filename == null) {
                throw IllegalArgumentException("For ByByteArray files inside media group, unique filename should be set")
            } else {
                listOf(tFile.fileBytes.toMultipartBodyPart(tFile.filename, tFile.filename, mediaType))
            }
        }
        else -> emptyList()
    }
}
