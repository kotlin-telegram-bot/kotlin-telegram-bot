package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

enum class ChatAction {
    @SerializedName("typing") TYPING,
    @SerializedName("upload_photo") UPLOAD_PHOTO,
    @SerializedName("record_video") RECORD_VIDEO,
    @SerializedName("upload_video") UPLOAD_VIDEO,
    @SerializedName("record_audio") RECORD_AUDIO,
    @SerializedName("upload_audio") UPLOAD_AUDIO,
    @SerializedName("upload_document") UPLOAD_DOCUMENT,
    @SerializedName("find_location") FIND_LOCATION,
    @SerializedName("record_video_note") RECORD_VIDEO_NOTE,
    @SerializedName("upload_video_note") UPLOAD_VIDEO_NOTE,
    @SerializedName("choose_sticker") CHOOSE_STICKER,
}
