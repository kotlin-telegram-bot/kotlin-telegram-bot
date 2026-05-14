# Kotlin Telegram Bot
[![Build Status](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/actions/workflows/ci.yml?query=branch%3Amain)
[![Release](https://jitpack.io/v/kotlin-telegram-bot/kotlin-telegram-bot.svg)](https://jitpack.io/#kotlin-telegram-bot/kotlin-telegram-bot)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

A wrapper for the Telegram Bot API written in Kotlin.

## Usage

Creating a bot instance is really simple:

```kotlin
fun main() {
    val bot = bot {
        token = "YOUR_API_KEY"
    }
}
```

Now lets poll telegram API and route all text updates:

```kotlin
fun main() {
    val bot = bot {
        token = "YOUR_API_KEY"
        dispatch {
            text {
                bot.sendMessage(ChatId.fromId(message.chat.id), text = text)
            }
        }
    }
    bot.startPolling()
}
```

Want to route commands?:

```kotlin
fun main() {
    val bot = bot {
        token = "YOUR_API_KEY"
        dispatch {
            command("start") {
                val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                result.fold({
                    // do something here with the response
                },{
                    // do something with the error 
                })
            }
        }
    }
    bot.startPolling()
}
```

## Examples
Take a look at the [examples folder](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/samples).

There are several samples:
* A simple echo bot
* A more complex sample with commands, filter, reply markup keyboard and more
* A sample getting updates through Telegram's webhook using a Netty server
* An example bot sending polls and listening to poll answers

## Download
+ Add the JitPack repository to your root build.gradle file:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

+ Add the code to your **module**'s build.gradle file:

```gradle
dependencies {
    implementation 'io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:x.y.z'
}
```

## Upgrading from 6.x

The current line (`10.x`) is up to date with Bot API 10.0 and includes the breaking changes Telegram introduced between 6.3 and 10.0. If you're coming from 6.2.0 or earlier, read [`docs/MIGRATION_10.md`](docs/MIGRATION_10.md) before bumping — most code will keep compiling, but `getChat` now returns `ChatFullInfo` (not `Chat`), the legacy reply / forward / preview parameters have richer replacements, and the duplicate `entities.SuccessfulPayment` was removed.

## Detailed documentation

**Core**

- [Getting updates](docs/gettingUpdates.md) — polling, webhooks, allowed update types.
- [Webhooks](docs/webhooks.md) — `webhook { }` DSL, `secretToken`, manual `processUpdate`.
- [Error handling](docs/errorHandling.md) — `TelegramBotResult` and `telegramError { }`.
- [Logging](docs/logging.md).
- [File uploads](docs/fileUploads.md) — `TelegramFile.ByFile` / `ByByteArray` / `ByInputStream` / `ByUrl` / `ByFileId`.

**Chat and messages**

- [Chat and ChatFullInfo](docs/chat.md) — the 7.3 split.
- [Reply parameters](docs/replyParameters.md) — quoting, cross-chat replies.
- [Link preview options](docs/linkPreviewOptions.md) — custom URL, size, position.
- [Forum topics](docs/forumTopics.md) — create / edit / close / delete; General topic.
- [Reactions](docs/reactions.md) — set, delete, paid reactions.

**Built-in content**

- [Polls](docs/polls.md).
- [Dice](docs/dice.md).
- [Games](docs/games.md).

**Telegram Stars and monetisation**

- [Telegram Stars](docs/telegramStars.md) — invoices, refunds, transactions.
- [Paid media](docs/paidMedia.md) — `sendPaidMedia`, purchases.
- [Gifts](docs/gifts.md) — sending and managing.

**Business bots**

- [Business connection](docs/businessConnection.md) — connecting, sending on behalf, account management.
- [Stories](docs/stories.md) — `postStory`, `editStory`, `deleteStory`, clickable areas.

## Contributing

 1. **Fork** and **clone** the repo
 2. Run `./gradlew ktlintFormat`
 3. Run `./gradlew build` to see if tests, [ktlint](https://github.com/pinterest/ktlint) and [abi checks](https://github.com/Kotlin/binary-compatibility-validator) pass.  
 4. **Commit** and **push** your changes
 5. Submit a **pull request** to get your changes reviewed

## Thanks
- Big part of the architecture of this project is inspired by [python-telegram-bot](https://github.com/python-telegram-bot/python-telegram-bot), check it out!
- Some awesome kotlin ninja techniques were grabbed from [Fuel](https://github.com/kittinunf/Fuel).

## License
Kotlin Telegram Bot is under the Apache 2.0 license. See the [LICENSE](LICENSE) for more information.
