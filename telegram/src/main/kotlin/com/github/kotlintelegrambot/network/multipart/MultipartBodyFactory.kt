package com.github.kotlintelegrambot.network.multipart

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaVideo
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.network.ApiConstants
import com.github.kotlintelegrambot.network.MediaTypeConstants
import com.google.gson.Gson
import java.io.File
import okhttp3.MultipartBody

class MultipartBodyFactory(private val gson: Gson) {

    fun createForSendMediaGroup(
        chatId: Long,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): List<MultipartBody.Part> {
        val chatIdPart = chatId.toMultipartBodyPart(ApiConstants.CHAT_ID)
        return createSendMediaGroupMultipartBody(chatIdPart, mediaGroup, disableNotification, replyToMessageId)
    }

    fun createForSendMediaGroup(
        chatId: String,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): List<MultipartBody.Part> {
        val chatIdPart = chatId.toMultipartBodyPart(ApiConstants.CHAT_ID)
        return createSendMediaGroupMultipartBody(chatIdPart, mediaGroup, disableNotification, replyToMessageId)
    }

    private fun createSendMediaGroupMultipartBody(
        chatIdPart: MultipartBody.Part,
        mediaGroup: MediaGroup,
        disableNotification: Boolean? = null,
        replyToMessageId: Long? = null
    ): List<MultipartBody.Part> {
        val filesParts = mediaGroup.takeFiles().map { (file, mediaType) ->
            file.toMultipartBodyPart(mediaType = mediaType)
        }
        val mediaGroupPart = gson.toJson(mediaGroup.medias).toMultipartBodyPart(ApiConstants.SendMediaGroup.MEDIA)
        val disableNotificationPart = disableNotification?.toMultipartBodyPart(ApiConstants.DISABLE_NOTIFICATION)
        val replayToMessageId = replyToMessageId?.toMultipartBodyPart(ApiConstants.REPLY_TO_MESSAGE_ID)

        return listOfNotNull(chatIdPart, mediaGroupPart, disableNotificationPart, replayToMessageId) + filesParts
    }

    private fun MediaGroup.takeFiles(): List<Pair<File, String>> = medias.flatMap { groupableMedia ->
        when {
            groupableMedia is InputMediaPhoto && groupableMedia.media is TelegramFile.ByFile -> listOf(
                groupableMedia.media.file to MediaTypeConstants.IMAGE
            )
            groupableMedia is InputMediaVideo && groupableMedia.media is TelegramFile.ByFile && groupableMedia.thumb != null -> listOf(
                groupableMedia.media.file to MediaTypeConstants.VIDEO,
                groupableMedia.thumb.file to MediaTypeConstants.IMAGE
            )
            groupableMedia is InputMediaVideo && groupableMedia.media is TelegramFile.ByFile -> listOf(
                groupableMedia.media.file to MediaTypeConstants.VIDEO
            )
            groupableMedia is InputMediaVideo && groupableMedia.thumb != null -> listOf(
                groupableMedia.thumb.file to MediaTypeConstants.IMAGE
            )
            else -> emptyList()
        }
    }
}
