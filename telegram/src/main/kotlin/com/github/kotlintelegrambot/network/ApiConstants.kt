package com.github.kotlintelegrambot.network

internal object ApiConstants {
    const val CHAT_ID = "chat_id"
    const val USER_ID = "user_id"
    const val DISABLE_NOTIFICATION = "disable_notification"
    const val REPLY_TO_MESSAGE_ID = "reply_to_message_id"
    const val REPLY_MARKUP = "reply_markup"

    object SendMediaGroup {
        const val MEDIA = "media"
    }

    object SetWebhook {
        const val URL = "url"
        const val CERTIFICATE = "certificate"
        const val MAX_CONNECTIONS = "max_connections"
        const val ALLOWED_UPDATES = "allowed_updates"
    }

    object SetChatAdministratorCustomTitle {
        const val OP_NAME = "setChatAdministratorCustomTitle"
        const val CUSTOM_TITLE = "custom_title"
    }
}
