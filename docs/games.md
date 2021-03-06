# Send Games

Your bot can offer users HTML5 games to play solo or to compete against each other in groups and one-on-one chats. 
Create games via [@BotFather](https://t.me/botfather) using the */newgame* command.

```kotlin
// List games on command "/list-games"
val bot = bot {
    token = BOT_API_TOKEN
    
    dispatch {
        command("list-games") {
            val gamesNames = listOf("GAME_SHORT_NAME")
            gamesNames.forEach { bot.sendGame(chatId = ChatId.fromId(message.chat.id), gameShortName = it) }
        }
    }
}
```

You can further customize the message sent by using the following parameters:

 - **disableNotification:** Sends the message silently. Users will receive a notification with no sound.
 - **replyToMessageId:** If the message is a reply, ID of the original message
 - **allowSendingWithoutReply:** Pass True, if the message should be sent even if the specified replied-to message is not found
 - **replyMarkup:** A JSON-serialized object for an inline keyboard. If empty, one 'Play game_title' button will be shown. If not empty, the first button must launch the game.

```kotlin
// 'Play game_title' button will be shown
bot.sendGame(
        chatId = CHAT_ID,
        gameShortName = GAME_SHORT_NAME,
        disableNotification = true,
        replyToMessageId = MESSAGE_ID,
        allowSendingWithoutReply = true
)

// You can customize your own set of buttons, but remember the first one has to be the one to launch the game
bot.sendGame(
        chatId = CHAT_ID,
        gameShortName = GAME_SHORT_NAME,
        replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
                InlineKeyboardButton.Url(
                        text = "Play My Game",
                        url = GAME_URL
                ),
                InlineKeyboardButton.CallbackData(
                        text = "Button Example",
                        callbackData = SOME_DATA
                )
        )
)
```