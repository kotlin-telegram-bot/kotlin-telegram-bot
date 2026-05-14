package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.google.gson.annotations.SerializedName as Name

/**
 * Contains information about a user that was shared with the bot using a
 * [KeyboardButtonRequestUsers] button.
 *
 * Bot API 7.2.
 *
 * See https://core.telegram.org/bots/api#shareduser
 */
data class SharedUser(
    @Name("user_id") val userId: Long,
    @Name("first_name") val firstName: String? = null,
    @Name("last_name") val lastName: String? = null,
    @Name("username") val username: String? = null,
    @Name("photo") val photo: List<PhotoSize>? = null,
)

/**
 * Contains information about a user whose identifier was shared with the bot using a
 * [KeyboardButtonRequestUser] button.
 *
 * Bot API 6.5. Deprecated in 7.0 in favor of [UsersShared].
 *
 * See https://core.telegram.org/bots/api#usershared
 */
@Deprecated(
    message = "Use UsersShared instead (Bot API 7.0).",
    replaceWith = ReplaceWith("UsersShared"),
)
data class UserShared(
    @Name("request_id") val requestId: Int,
    @Name("user_id") val userId: Long,
)

/**
 * Contains information about the users whose identifiers were shared with the bot using a
 * [KeyboardButtonRequestUsers] button.
 *
 * Bot API 7.0. In 7.2 the `user_ids` field was deprecated in favor of `users` carrying
 * [SharedUser] objects. Both fields are exposed here for compatibility.
 *
 * See https://core.telegram.org/bots/api#usersshared
 */
data class UsersShared(
    @Name("request_id") val requestId: Int,
    @Name("users") val users: List<SharedUser>? = null,
    @Deprecated("Replaced by users (List<SharedUser>) in Bot API 7.2.")
    @Name("user_ids")
    val userIds: List<Long>? = null,
)
