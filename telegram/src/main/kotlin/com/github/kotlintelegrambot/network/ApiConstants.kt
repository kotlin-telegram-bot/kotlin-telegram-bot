package com.github.kotlintelegrambot.network

object ApiConstants {
    const val CHAT_ID = "chat_id"

    object SendMediaGroup {
        const val MEDIA = "media"
        const val DISABLE_NOTIFICATION = "disable_notification"
        const val REPLY_TO_MESSAGE_ID = "reply_to_message_id"
    }

    object SetWebhook {
        const val URL = "url"
        const val CERTIFICATE = "certificate"
        const val MAX_CONNECTIONS = "max_connections"
        const val ALLOWED_UPDATES = "allowed_updates"
    }
}
