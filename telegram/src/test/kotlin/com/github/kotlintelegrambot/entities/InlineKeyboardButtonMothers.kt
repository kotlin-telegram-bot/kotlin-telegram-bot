package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

private const val ANY_TEXT = "Button text"
private const val ANY_URL = "https://www.github.com"
private const val ANY_CALLBACK_DATA = "callback data"
private const val ANY_INLINE_QUERY = "inline query"

fun anyInlineKeyboardButtonUrl(
    text: String = ANY_TEXT,
    url: String = ANY_URL
): InlineKeyboardButton.Url = InlineKeyboardButton.Url(text, url)

fun anyInlineKeyboardButtonCallbackData(
    text: String = ANY_TEXT,
    callbackData: String = ANY_CALLBACK_DATA
): InlineKeyboardButton.CallbackData = InlineKeyboardButton.CallbackData(text, callbackData)

fun anyInlineKeyboardButtonSwitchInlineQuery(
    text: String = ANY_TEXT,
    switchInlineQuery: String = ANY_INLINE_QUERY
): InlineKeyboardButton.SwitchInlineQuery = InlineKeyboardButton.SwitchInlineQuery(text, switchInlineQuery)

fun anyInlineKeyboardButtonSwitchInlineQueryCurrentChat(
    text: String = ANY_TEXT,
    switchInlineQueryCurrentChat: String = ANY_INLINE_QUERY
): InlineKeyboardButton.SwitchInlineQueryCurrentChat = InlineKeyboardButton.SwitchInlineQueryCurrentChat(
    text,
    switchInlineQueryCurrentChat
)

fun anyInlineKeyboardButtonCallbackGameButtonType(
    text: String = ANY_TEXT,
    callbackGame: CallbackGame = CallbackGame()
): InlineKeyboardButton.CallbackGameButtonType = InlineKeyboardButton.CallbackGameButtonType(
    text,
    callbackGame
)

fun anyInlineKeyboardButtonPay(
    text: String = ANY_TEXT
): InlineKeyboardButton.Pay = InlineKeyboardButton.Pay(text)
