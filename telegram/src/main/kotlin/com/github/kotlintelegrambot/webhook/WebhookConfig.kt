package com.github.kotlintelegrambot.webhook

import com.github.kotlintelegrambot.entities.TelegramFile

class WebhookConfigBuilder {
    var url: String? = null
    var certificate: TelegramFile? = null
    var maxConnections: Int? = null
    var allowedUpdates: List<String>? = null

    fun build(): WebhookConfig {
        val finalUrl = url ?: error("You must provide a url for the webhook")
        return WebhookConfig(finalUrl, certificate, maxConnections, allowedUpdates)
    }
}

data class WebhookConfig(
    val url: String,
    val certificate: TelegramFile? = null,
    val maxConnections: Int? = null,
    val allowedUpdates: List<String>? = null
)
