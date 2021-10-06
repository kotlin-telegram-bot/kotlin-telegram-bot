package com.github.kotlintelegrambot.network.multipart

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaAudio
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
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean?
    ): List<MultipartBody.Part> {
        val chatIdString = ChatIdConverterFactory.chatIdToString(chatId)
        val chatIdPart = chatIdString.toMultipartBodyPart(ApiConstants.CHAT_ID)
        return createSendMediaGroupMultipartBody(chatIdPart, mediaGroup, disableNotification, replyToMessageId, allowSendingWithoutReply)
    }

    private fun createSendMediaGroupMultipartBody(
        chatIdPart: MultipartBody.Part,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean?
    ): List<MultipartBody.Part> {
        val filesParts = mediaGroup.takeFilesParts()
        val mediaGroupPart = gson.toJson(mediaGroup.medias).toMultipartBodyPart(ApiConstants.SendMediaGroup.MEDIA)
        val disableNotificationPart = disableNotification?.toMultipartBodyPart(ApiConstants.DISABLE_NOTIFICATION)
        val replyToMessageIdPart = replyToMessageId?.toMultipartBodyPart(ApiConstants.REPLY_TO_MESSAGE_ID)
        val allowSendingWithoutReplyPart = allowSendingWithoutReply?.toMultipartBodyPart(ApiConstants.ALLOW_SENDING_WITHOUT_REPLY)

        return listOfNotNull(chatIdPart, mediaGroupPart, disableNotificationPart, replyToMessageIdPart, allowSendingWithoutReplyPart) + filesParts
    }

    private fun MediaGroup.takeFilesParts(): List<MultipartBody.Part> = medias.flatMap { groupableMedia ->
        val mediaType = when (groupableMedia) {
            is InputMediaPhoto -> MediaTypeConstants.IMAGE
            is InputMediaVideo -> MediaTypeConstants.VIDEO
            is InputMediaAudio -> MediaTypeConstants.AUDIO
            else -> null
        }
        mutableListOf<MultipartBody.Part>().apply {
            when (val media = groupableMedia.media) {
                is TelegramFile.ByFile -> add(media.file.toMultipartBodyPart(mediaType = mediaType))
                is TelegramFile.ByByteArray -> add(media.fileBytes.toMultipartBodyPart(partName = media.filename!!, mediaType = mediaType))
            }
            if (groupableMedia is InputMediaVideo && groupableMedia.thumb != null) {
                add(groupableMedia.thumb.file.toMultipartBodyPart(mediaType = MediaTypeConstants.IMAGE))
            }
        }.toList()
    }
}
