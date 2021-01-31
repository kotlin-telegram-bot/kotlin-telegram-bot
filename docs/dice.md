# Dice

The Telegram Bot API offers one operation to send a die (dice) to a given chat. The operation is called `sendDice` and in this library you can use it by directly calling the `sendDice` method of a bot instance. For example, you can send a dice in the next way:

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

bot.sendDice(ANY_CHAT_ID)
```

The operation also let you customize the emoji that will be used for the dice (currently 🎲 or 🎯). You can customize it in the next way:

```kotlin
// to send the dice with a dice emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.Dice)

// to send the dice with a dartboard emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.Dartboard)

// to send the dice with a basketball emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.Basketball)

// to send the dice with a football emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.Football)

// to send the dice with a slot machine emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.SlotMachine)

// to send the dice with a bowling emoji
bot.sendDice(ANY_CHAT_ID, DiceEmoji.Bowling)
```

Moreover, it's also possible to listen to dice messages. These dice messages come within an update object and you can listen to them with the library's DSL in the next way:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        dice {
            // do whatever you want with the dice message
        }               
    }
}
```
