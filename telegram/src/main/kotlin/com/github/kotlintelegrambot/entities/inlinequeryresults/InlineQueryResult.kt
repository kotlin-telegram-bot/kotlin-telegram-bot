package com.github.kotlintelegrambot.entities.inlinequeryresults

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName

enum class MimeType(val rawName: String) {
    @SerializedName("text/html")
    TEXT_HTML("text/html"),
    @SerializedName("video/mp4")
    VIDEO_MP4("video/mp4"),
    @SerializedName("application/pdf")
    APPLICATION_PDF("application/pdf"),
    @SerializedName("application/zip")
    APPLICATION_ZIP("application/zip"),
    @SerializedName("image/jpeg")
    IMAGE_JPEG("image/jpeg"),
    @SerializedName("image/gif")
    IMAGE_GIF("image/gif")
}

fun String.toMimeType(): MimeType? =
    MimeType.values().firstOrNull { type -> this == type.rawName }

private object QueryResultTypes {
    const val ARTICLE = "article"
    const val PHOTO = "photo"
    const val VIDEO = "video"
    const val VOICE = "voice"
    const val STICKER = "sticker"
    const val MPEG4_GIF = "mpeg4_gif"
    const val GIF = "gif"
    const val AUDIO = "audio"
    const val DOCUMENT = "document"
    const val LOCATION = "location"
    const val VENUE = "venue"
    const val CONTACT = "contact"
    const val GAME = "game"
}

sealed class InlineQueryResult(
    val type: String
) {
    abstract val id: String
    abstract val replyMarkup: InlineKeyboardMarkup?

    data class Article(
        override val id: String,
        val title: String,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        val url: String? = null,
        @SerializedName("hide_url") val hideUrl: Boolean? = null,
        val description: String? = null,
        @SerializedName("thumb_url") val thumbUrl: String? = null,
        @SerializedName("thumb_width") val thumbWidth: Int? = null,
        @SerializedName("thumb_height") val thumbHeight: Int? = null
    ) : InlineQueryResult(QueryResultTypes.ARTICLE)

    data class Photo(
        override val id: String,
        @SerializedName("photo_url") val photoUrl: String,
        @SerializedName("thumb_url") val thumbUrl: String,
        @SerializedName("photo_width") val photoWidth: Int? = null,
        @SerializedName("photo_height") val photoHeight: Int? = null,
        val title: String? = null,
        val description: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.PHOTO)

    data class Gif(
        override val id: String,
        @SerializedName("gif_url") val gifUrl: String,
        @SerializedName("gif_width") val gifWidth: Int? = null,
        @SerializedName("gif_height") val gifHeight: Int? = null,
        @SerializedName("gif_duration") val gifDuration: Int? = null,
        @SerializedName("thumb_url") val thumbUrl: String,
        @SerializedName("thumb_mime_type") val thumbMimeType: MimeType? = null,
        val title: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.GIF)

    data class Mpeg4Gif(
        override val id: String,
        @SerializedName("mpeg4_url") val mpeg4Url: String,
        @SerializedName("mpeg4_width") val mpeg4Width: Int? = null,
        @SerializedName("mpeg4_height") val mpeg4Height: Int? = null,
        @SerializedName("mpeg4_duration") val mpeg4Duration: Int? = null,
        @SerializedName("thumb_url") val thumbUrl: String,
        @SerializedName("thumb_mime_type") val thumbMimeType: MimeType? = null,
        val title: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.MPEG4_GIF)

    data class Video(
        override val id: String,
        @SerializedName("video_url") val videoUrl: String,
        @SerializedName("mime_type") val mimeType: MimeType,
        @SerializedName("thumb_url") val thumbUrl: String,
        val title: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("video_width") val videoWidth: Int? = null,
        @SerializedName("video_height") val videoHeight: Int? = null,
        @SerializedName("video_duration") val videoDuration: Int? = null,
        val description: String? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.VIDEO)

    data class Audio(
        override val id: String,
        @SerializedName("audio_url") val audioUrl: String,
        val title: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        val performer: String? = null,
        @SerializedName("audio_duration") val audioDuration: Int? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.AUDIO)

    data class Voice(
        override val id: String,
        @SerializedName("voice_url") val voiceUrl: String,
        val title: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("voice_duration") val voiceDuration: Int? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.VOICE)

    data class Document(
        override val id: String,
        val title: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("document_url") val documentUrl: String,
        @SerializedName("mime_type") val mimeType: MimeType,
        val description: String? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerializedName("thumb_url") val thumbUrl: String? = null,
        @SerializedName("thumb_width") val thumbWidth: Int? = null,
        @SerializedName("thumb_height") val thumbHeight: Int? = null
    ) : InlineQueryResult(QueryResultTypes.DOCUMENT)

    data class Location(
        override val id: String,
        val latitude: Float,
        val longitude: Float,
        val title: String,
        @SerializedName("live_period") val livePeriod: Int? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerializedName("thumb_url") val thumbUrl: String? = null,
        @SerializedName("thumb_width") val thumbWidth: Int? = null,
        @SerializedName("thumb_height") val thumbHeight: Int? = null,
        @SerializedName("proximity_alert_radius") val proximityAlertRadius: Int? = null
    ) : InlineQueryResult(QueryResultTypes.LOCATION)

    data class Venue(
        override val id: String,
        val latitude: Float,
        val longitude: Float,
        val title: String,
        val address: String,
        @SerializedName("foursquare_id") val foursquareId: String? = null,
        @SerializedName("foursquare_type") val foursquareType: String? = null,
        @SerializedName("google_place_id") val googlePlaceId: String? = null,
        @SerializedName("google_place_type") val googlePlaceType: String? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerializedName("thumb_url") val thumbUrl: String? = null,
        @SerializedName("thumb_width") val thumbWidth: Int? = null,
        @SerializedName("thumb_height") val thumbHeight: Int? = null
    ) : InlineQueryResult(QueryResultTypes.VENUE)

    data class Contact(
        override val id: String,
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String? = null,
        val vcard: String? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerializedName("thumb_url") val thumbUrl: String? = null,
        @SerializedName("thumb_width") val thumbWidth: Int? = null,
        @SerializedName("thumb_height") val thumbHeight: Int? = null
    ) : InlineQueryResult(QueryResultTypes.CONTACT)

    data class Game(
        override val id: String,
        @SerializedName("game_short_name") val gameShortName: String,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null
    ) : InlineQueryResult(QueryResultTypes.GAME)

    data class CachedAudio(
        override val id: String,
        @SerializedName("audio_file_id") val audioFileId: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.AUDIO)

    data class CachedDocument(
        override val id: String,
        val title: String,
        @SerializedName("document_file_id") val documentFileId: String,
        val description: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.DOCUMENT)

    data class CachedGif(
        override val id: String,
        @SerializedName("gif_file_id") val gifFileId: String,
        val title: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.GIF)

    data class CachedMpeg4Gif(
        override val id: String,
        @SerializedName("mpeg4_file_id") val mpeg4FileId: String,
        val title: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.MPEG4_GIF)

    data class CachedPhoto(
        override val id: String,
        @SerializedName("photo_file_id") val photoFileId: String,
        val title: String? = null,
        val description: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.PHOTO)

    data class CachedSticker(
        override val id: String,
        @SerializedName("sticker_file_id") val stickerFileId: String,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.STICKER)

    data class CachedVideo(
        override val id: String,
        @SerializedName("video_file_id") val videoFileId: String,
        val title: String,
        val description: String? = null,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.VIDEO)

    data class CachedVoice(
        override val id: String,
        @SerializedName("voice_file_id") val voiceFileId: String,
        val title: String,
        val caption: String? = null,
        @SerializedName("parse_mode") val parseMode: ParseMode? = null,
        @SerializedName("reply_markup") override val replyMarkup: InlineKeyboardMarkup? = null,
        @SerializedName("input_message_content") val inputMessageContent: InputMessageContent? = null
    ) : InlineQueryResult(QueryResultTypes.VOICE)
}
