package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class WebhookInfo(
    @SerializedName("url") val url: String,
    @SerializedName("has_custom_certificate") val hasCustomCertificate: Boolean,
    @SerializedName("pending_update_count") val pendingUpdateCount: Int,
    @SerializedName("ip_address") val ipAddress: String?,
    @SerializedName("last_error_date") val lastErrorDate: Long?,
    @SerializedName("last_error_message") val lastErrorMessage: String?,
    @SerializedName("max_connections") val maxConnections: Int?,
    @SerializedName("allowed_updates") val allowedUpdates: List<String>?
)
