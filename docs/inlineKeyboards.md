# Inline keyboards

An inline keyboard is a grid of buttons attached directly to a message. In this library you build one with `InlineKeyboardMarkup.create(...)`, which accepts either a `List<List<InlineKeyboardButton>>` or a `vararg` of rows — each inner list is one row, so the layout you pass is the layout the user sees. There are also convenience factories `createSingleButton` and `createSingleRowKeyboard` when you only need a single button or row.

```kotlin
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.CopyTextButton
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.LoginUrl
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo

val keyboard = InlineKeyboardMarkup.create(
    listOf(
        InlineKeyboardButton.Url(text = "Open docs", url = "https://core.telegram.org/bots/api"),
        InlineKeyboardButton.CallbackData(text = "Like", callbackData = "like"),
    ),
    listOf(
        InlineKeyboardButton.SwitchInlineQuery(text = "Share", switchInlineQuery = "look at this"),
        InlineKeyboardButton.SwitchInlineQueryCurrentChat(text = "Help here", switchInlineQueryCurrentChat = "help"),
    ),
    listOf(
        InlineKeyboardButton.WebApp(text = "Open app", webApp = WebAppInfo("https://example.com/app")),
        InlineKeyboardButton.LoginUrlButtonType(
            text = "Sign in",
            loginUrl = LoginUrl(url = "https://example.com/auth", forwardText = null, botUsername = null),
        ),
    ),
    listOf(
        InlineKeyboardButton.CopyText(text = "Copy code", copyText = CopyTextButton("PROMO-42")),
    ),
)

bot.sendMessage(chatId, text = "Pick one:", replyMarkup = keyboard)
```

Two button variants must always sit at position `(0, 0)` and only one of them is allowed per keyboard: `InlineKeyboardButton.Pay` (used inside invoice messages) and `InlineKeyboardButton.CallbackGameButtonType` (used to launch a game). The `InlineKeyboardMarkup` initializer validates this for you and throws if you put either button anywhere else. `CopyText` was added in Bot API 7.11 and is wrapped through a `CopyTextButton(text)` payload.

Pressing a `CallbackData` button delivers a `CallbackQuery` update. Match on the data value from the dispatcher DSL and call `answerCallbackQuery` so Telegram stops the spinning loader on the client — you can also pass an alert text or a URL through the DSL parameters:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        callbackQuery("like") {
            bot.answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                text = "Thanks for the like!",
                showAlert = false,
            )
        }
    }
}
```

Once a message has been sent you can swap its keyboard (or remove it entirely by passing `null`) without re-sending the message. This is the canonical way to build wizards or paginated views:

```kotlin
bot.editMessageReplyMarkup(
    chatId = chatId,
    messageId = messageId,
    replyMarkup = InlineKeyboardMarkup.createSingleButton(
        InlineKeyboardButton.CallbackData(text = "Next page", callbackData = "page:2"),
    ),
)
```

For the full button schema, see https://core.telegram.org/bots/api#inlinekeyboardmarkup.
