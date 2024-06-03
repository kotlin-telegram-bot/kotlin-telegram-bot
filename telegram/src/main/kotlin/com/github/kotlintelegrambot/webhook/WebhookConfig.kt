package com.github.kotlintelegrambot.webhook

import com.github.kotlintelegrambot.entities.TelegramFile

class WebhookConfigBuilder {
    var createOnStart: Boolean? = null
    var url: String? = null
    var certificate: TelegramFile? = null
    var ipAddress: String? = null
    var maxConnections: Int? = null
    var allowedUpdates: List<String>? = null
    var dropPendingUpdates: Boolean? = null
    var secretToken: String? = null

    internal fun build(): WebhookConfig {
        val finalUrl = url ?: error("You must provide a url for the webhook")
        return WebhookConfig(createOnStart ?: true, finalUrl, certificate, ipAddress, maxConnections, allowedUpdates, dropPendingUpdates, secretToken)
    }
}

data class WebhookConfig(
    val createOnStart: Boolean = true,
    val url: String,
    val certificate: TelegramFile? = null,
    val ipAddress: String? = null,
    val maxConnections: Int? = null,
    val allowedUpdates: List<String>? = null,
    val dropPendingUpdates: Boolean? = null,
    val secretToken: String? = null,
)
