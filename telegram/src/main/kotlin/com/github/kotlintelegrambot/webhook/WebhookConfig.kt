package com.github.kotlintelegrambot.webhook

import com.github.kotlintelegrambot.entities.TelegramFile

public class WebhookConfigBuilder {
    public var url: String? = null
    public var certificate: TelegramFile? = null
    public var ipAddress: String? = null
    public var maxConnections: Int? = null
    public var allowedUpdates: List<String>? = null

    internal fun build(): WebhookConfig {
        val finalUrl = url ?: error("You must provide a url for the webhook")
        return WebhookConfig(finalUrl, certificate, ipAddress, maxConnections, allowedUpdates)
    }
}

public data class WebhookConfig(
    val url: String,
    val certificate: TelegramFile? = null,
    val ipAddress: String? = null,
    val maxConnections: Int? = null,
    val allowedUpdates: List<String>? = null
)
