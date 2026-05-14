package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.LivePhoto
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.polls.Poll
import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.google.gson.annotations.SerializedName

/**
 * Contains information about a message that is being replied to, which may come from another chat
 * or forum topic. (Bot API 7.0)
 *
 * See https://core.telegram.org/bots/api#externalreplyinfo
 */
data class ExternalReplyInfo(
    @SerializedName("origin") val origin: MessageOrigin,
    @SerializedName("chat") val chat: Chat? = null,
    @SerializedName("message_id") val messageId: Long? = null,
    @SerializedName("link_preview_options") val linkPreviewOptions: LinkPreviewOptions? = null,
    @SerializedName("animation") val animation: Animation? = null,
    @SerializedName("audio") val audio: Audio? = null,
    @SerializedName("document") val document: Document? = null,
    @SerializedName("paid_media") val paidMedia: com.github.kotlintelegrambot.entities.payments.PaidMediaInfo? = null,
    @SerializedName("photo") val photo: List<PhotoSize>? = null,
    @SerializedName("sticker") val sticker: Sticker? = null,
    @SerializedName("story") val story: Story? = null,
    @SerializedName("video") val video: Video? = null,
    @SerializedName("video_note") val videoNote: VideoNote? = null,
    @SerializedName("voice") val voice: Voice? = null,
    @SerializedName("live_photo") val livePhoto: LivePhoto? = null,
    @SerializedName("has_media_spoiler") val hasMediaSpoiler: Boolean? = null,
    @SerializedName("contact") val contact: Contact? = null,
    @SerializedName("dice") val dice: Dice? = null,
    @SerializedName("game") val game: Game? = null,
    @SerializedName("giveaway") val giveaway: Giveaway? = null,
    @SerializedName("giveaway_winners") val giveawayWinners: GiveawayWinners? = null,
    @SerializedName("invoice") val invoice: Invoice? = null,
    @SerializedName("location") val location: Location? = null,
    @SerializedName("poll") val poll: Poll? = null,
    @SerializedName("venue") val venue: Venue? = null,
)
