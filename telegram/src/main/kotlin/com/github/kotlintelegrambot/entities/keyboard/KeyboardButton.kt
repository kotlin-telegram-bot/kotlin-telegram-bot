package com.github.kotlintelegrambot.entities.keyboard

import com.github.kotlintelegrambot.entities.polls.PollType
import com.google.gson.annotations.SerializedName

/**
 * Represents one button of the reply keyboard.
 * For simple text buttons String can be used instead of this object to specify text of the button.
 * Optional fields requestContact, requestLocation, and requestPoll are mutually exclusive.
 * https://core.telegram.org/bots/api#keyboardbutton
 */
data class KeyboardButton(
    @SerializedName(KeyboardFields.TEXT) val text: String,
    @SerializedName(KeyboardFields.REQUEST_CONTACT) val requestContact: Boolean? = null,
    @SerializedName(KeyboardFields.REQUEST_LOCATION) val requestLocation: Boolean? = null,
    @SerializedName(KeyboardFields.REQUEST_POLL) val requestPoll: KeyboardButtonPollType? = null,
    @SerializedName(KeyboardFields.WEB_APP) val webApp: WebAppInfo? = null
)

/**
 * Represents type of a poll, which is allowed to be created and sent when the corresponding button is pressed.
 * https://core.telegram.org/bots/api#keyboardbuttonpolltype
 */
data class KeyboardButtonPollType(
    @SerializedName(KeyboardFields.TYPE) val type: PollType? = null
)
