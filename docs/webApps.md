# Web Apps (Mini Apps)

Telegram Web Apps — also known as Mini Apps — let a bot open a full HTML/JS interface inside the Telegram client. They were introduced in Bot API 6.0 and have been extended through every subsequent release. This library supports the bot-side surface of the feature: attaching Mini App buttons to keyboards, answering inline web-app queries, receiving data the Mini App sends back to the chat, and configuring the per-chat menu button.

The most common entry point is a button. Reply-keyboard buttons accept a `webApp` parameter, while inline keyboards have a dedicated `InlineKeyboardButton.WebApp` subtype. Both wrap a `WebAppInfo`, which only carries the URL of the Mini App:

```kotlin
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo

val replyButton = KeyboardButton(
    text = "Open shop",
    webApp = WebAppInfo("https://example.com/app"),
)

val inlineButton = InlineKeyboardButton.WebApp(
    text = "Open shop",
    webApp = WebAppInfo("https://example.com/app"),
)
```

When the user picks an inline-mode result coming from a Mini App, the bot has to confirm it with `answerWebAppQuery`. The second argument is any `InlineQueryResult` — the message that will be posted on the user's behalf:

```kotlin
bot.answerWebAppQuery(
    webAppQueryId = webAppQuery.id,
    inlineQueryResult = result,
)
```

Mini Apps can also push data back into the chat via `Telegram.WebApp.sendData(...)`. The payload arrives on the next update as a regular message with `message.webAppData` set. The `data` field holds the raw string that the app sent; `buttonText` echoes the label of the button the user pressed:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            message.webAppData?.let { payload ->
                val raw = payload.data            // String? — the data the Mini App sent
                val buttonText = payload.buttonText
                // parse `raw` (often JSON) and react to it
            }
        }
    }
}
```

Finally, the bot's menu button — the small button next to the message composer — can be turned into a Mini App launcher with `setChatMenuButton`. Pass a `ChatId` to target a single user, or omit it to update the default menu button for every private chat:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.MenuButton
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo

bot.setChatMenuButton(
    chatId = ChatId.fromId(USER_ID),
    menuButton = MenuButton.WebApp(
        text = "Open shop",
        webApp = WebAppInfo("https://example.com/app"),
    ),
)
```

For the full Mini Apps platform documentation, see https://core.telegram.org/bots/webapps.
