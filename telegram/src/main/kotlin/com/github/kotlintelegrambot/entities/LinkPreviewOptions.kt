package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the options used for link preview generation for a message.
 *
 * Replaces the legacy `disable_web_page_preview` parameter on `sendMessage` and
 * `editMessageText` (Bot API 7.0). See https://core.telegram.org/bots/api#linkpreviewoptions
 *
 * @property isDisabled `true` if the link preview is disabled.
 * @property url URL to use for the link preview. If empty, then the first URL found in the message
 *   text will be used.
 * @property preferSmallMedia `true` if the media in the link preview is supposed to be shrunk;
 *   ignored if the URL isn't explicitly specified or media size change isn't supported for the
 *   preview.
 * @property preferLargeMedia `true` if the media in the link preview is supposed to be enlarged;
 *   ignored if the URL isn't explicitly specified or media size change isn't supported for the
 *   preview.
 * @property showAboveText `true` if the link preview must be shown above the message text;
 *   otherwise, the link preview will be shown below the message text.
 */
data class LinkPreviewOptions(
    @SerializedName("is_disabled") val isDisabled: Boolean? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("prefer_small_media") val preferSmallMedia: Boolean? = null,
    @SerializedName("prefer_large_media") val preferLargeMedia: Boolean? = null,
    @SerializedName("show_above_text") val showAboveText: Boolean? = null,
)
