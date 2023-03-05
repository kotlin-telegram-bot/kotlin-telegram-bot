package com.github.kotlintelegrambot.network

internal object ApiConstants {
    const val CHAT_ID = "chat_id"
    const val USER_ID = "user_id"
    const val DISABLE_CONTENT_TYPE_DETECTION = "disable_content_type_detection"
    const val DISABLE_NOTIFICATION = "disable_notification"
    const val REPLY_TO_MESSAGE_ID = "reply_to_message_id"
    const val ALLOW_SENDING_WITHOUT_REPLY = "allow_sending_without_reply"
    const val REPLY_MARKUP = "reply_markup"

    object SendMediaGroup {
        const val MEDIA = "media"
    }

    object SendGame {
        const val GAME_SHORT_NAME = "game_short_name"
    }

    object SetWebhook {
        const val URL = "url"
        const val CERTIFICATE = "certificate"
        const val IP_ADDRESS = "ip_address"
        const val MAX_CONNECTIONS = "max_connections"
        const val ALLOWED_UPDATES = "allowed_updates"
        const val DROP_PENDING_UPDATES = "drop_pending_updates"
    }

    object SetChatAdministratorCustomTitle {
        const val OP_NAME = "setChatAdministratorCustomTitle"
        const val CUSTOM_TITLE = "custom_title"
    }
}
