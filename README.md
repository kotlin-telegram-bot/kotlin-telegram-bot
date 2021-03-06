# Kotlin Telegram Bot
[![Build Status](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/workflows/Continuous%20Integration/badge.svg?branch=main)](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/actions?query=workflow%3A%22Continuous+Integration%22+branch%3Amain)
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

## Detailed documentation

1. [Getting updates](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/docs/gettingUpdates.md)
2. [Polls](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/docs/polls.md)
3. [Dice](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/docs/dice.md)
4. [Logging](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/docs/logging.md)
5. [Games](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/docs/games.md)

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
