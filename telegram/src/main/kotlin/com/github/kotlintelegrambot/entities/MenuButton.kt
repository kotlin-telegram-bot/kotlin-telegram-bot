package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo
import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the bot's menu button in a private chat.
 *
 * See https://core.telegram.org/bots/api#menubutton
 */
sealed class MenuButton {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /**
     * Represents a menu button, which opens the bot's list of commands.
     *
     * See https://core.telegram.org/bots/api#menubuttoncommands
     */
    data class Commands(
        @Name("type") override val type: String = "commands",
    ) : MenuButton()

    /**
     * Represents a menu button, which launches a Web App.
     *
     * See https://core.telegram.org/bots/api#menubuttonwebapp
     */
    data class WebApp(
        @Name("type") override val type: String = "web_app",
        @Name("text") val text: String,
        @Name("web_app") val webApp: WebAppInfo,
    ) : MenuButton()

    /**
     * Describes that no specific value for the menu button was set.
     *
     * See https://core.telegram.org/bots/api#menubuttondefault
     */
    data class Default(
        @Name("type") override val type: String = "default",
    ) : MenuButton()
}
