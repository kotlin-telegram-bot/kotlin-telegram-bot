# Dispatcher

The dispatcher is the entry point for reacting to updates. You configure it inside the `bot { ... }` builder via `dispatch { ... }`, and each handler is registered with a small DSL function. Handlers run in the order they were declared until one of them consumes the update or the list is exhausted, so put the more specific matchers first.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    dispatch {
        command("start") {
            bot.sendMessage(ChatId.fromId(message.chat.id), text = "Welcome!")
        }
        text("ping") {
            bot.sendMessage(ChatId.fromId(message.chat.id), text = "pong")
        }
        callbackQuery("like") { /* ... */ }
    }
}
```

The full set of handler DSLs exposed by the library lives in `HandlersDsl.kt`. For message-shaped updates: `message`, `text` (optional case-insensitive substring match), `textEdit` for edited messages, `command`, `contact`, `location`, `photos`, `audio`, `document`, `video`, `videoNote`, `voice`, `animation`, `sticker`, `game`, `dice`, `newChatMembers`, `leftChatMember`. For non-message updates: `callbackQuery`, `pollAnswer`, `preCheckoutQuery`, `inlineQuery`, `chosenInlineResult`, `myChatMember`, `chatMember`, `chatJoinRequest`, and `channel` for channel posts. Errors during update processing are routed to `telegramError`.

The plain `message` handler takes an optional `Filter`, which lets you narrow the update without writing a guard inside the body. Filters are combinable with `and`, `or` and `!`, and you can drop in a `Filter.Custom { ... }` when none of the built-ins fit. Useful predicates include `Filter.Text`, `Filter.Command`, `Filter.Reply`, `Filter.Forward`, `Filter.Photo`, `Filter.Sticker`, `Filter.Group`, `Filter.Private`, `Filter.Channel`, and the parameterized `Filter.Chat(id)` / `Filter.User(id)`.

```kotlin
import com.github.kotlintelegrambot.extensions.filters.Filter

dispatch {
    message(Filter.Text and Filter.Group) {
        // every non-command text message inside a group or supergroup
    }
    message(Filter.Photo and !Filter.Forward) {
        // original photos only, ignore forwards
    }
    message(Filter.Custom { from?.id == 42L }) {
        // anything from user 42
    }
}
```

If you want to stop later handlers from firing on the same update, call `update.consume()` from inside the handler body. `Update` extends `ConsumableObject`, and the dispatcher skips any update flagged as consumed:

```kotlin
dispatch {
    command("admin") {
        if (message.from?.id !in admins) {
            update.consume()
            return@command
        }
        // privileged path
    }
    message { /* won't run for /admin updates the previous handler consumed */ }
}
```

Each handler body has access to `bot`, the matched payload (`message`, `callbackQuery`, ...) and the raw `update` for anything the DSL doesn't expose directly. There is no separate spec link for the dispatcher itself — it's library-specific glue over `getUpdates`.
