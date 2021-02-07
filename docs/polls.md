# Polls

Telegram Bot API offers one operation to let you send polls to any chat. The operation is called `sendPoll` and in this library you can use it by directly calling the `sendPoll` method of your bot instance. Telegram polls has a lot of options and you can configure them all in the mentioned method. For example, you can send a quiz poll with non anonymous answers in the next way by using our library: 

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

bot.sendPoll(
    channelUsername = "@friends", 
    type = QUIZ,
    question = "Java or Kotlin?",
    options = listOf("Java", "Kotlin"),
    correctOptionId = 1, // index of the correct option,
    isAnonymous = false 
)
```

Moreover, it is also possible to listen to the answers of users in non-anonymous poll. These answers come as an update object and you can listen to them with the library's DSL in the next way:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        pollAnswer {
            // do whatever you want with the answer
        }               
    }
}
```

You can find an example bot sending polls and listening to poll answers in the [samples/polls](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot/tree/main/samples/polls) directory of this project.

If you want more information about Telegram polls, you can check the next links:

* https://core.telegram.org/bots/api#sendpoll
* https://telegram.org/blog/polls
* https://telegram.org/blog/polls-2-0-vmq
* https://telegram.org/blog/400-million#better-quizzes