# Forum Topics

Supergroups with the forum feature enabled can group conversations into topics. Every message belonging to a topic carries a `message_thread_id`, and your bot needs the `can_manage_topics` administrator right to create, edit, close or delete topics. The library exposes one method per Telegram Bot API operation, available directly on the bot instance.

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

val topic = bot.createForumTopic(
    chatId = ChatId.fromId(SUPERGROUP_ID),
    name = "Bugs",
    iconColor = 7322096,
).get()

bot.sendMessage(
    chatId = ChatId.fromId(SUPERGROUP_ID),
    text = "First message in the new topic",
    messageThreadId = topic.messageThreadId,
)
```

Once a topic exists you can rename it, close and reopen it, clear its pinned messages or remove it entirely:

```kotlin
val chatId = ChatId.fromId(SUPERGROUP_ID)
val threadId = topic.messageThreadId

bot.editForumTopic(chatId, threadId, name = "Bugs (triage)")
bot.closeForumTopic(chatId, threadId)
bot.reopenForumTopic(chatId, threadId)
bot.unpinAllForumTopicMessages(chatId, threadId)
bot.deleteForumTopic(chatId, threadId)
```

The "General" topic that every forum supergroup has by default is managed through a dedicated family of methods:

```kotlin
val chatId = ChatId.fromId(SUPERGROUP_ID)

bot.editGeneralForumTopic(chatId, name = "Lobby")
bot.closeGeneralForumTopic(chatId)
bot.reopenGeneralForumTopic(chatId)
bot.hideGeneralForumTopic(chatId)
bot.unhideGeneralForumTopic(chatId)
```

Telegram delivers forum lifecycle events as service messages, so they arrive as regular `Message` updates with one of the topic-specific fields set. You can react to them from the dispatcher DSL:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            when {
                message.forumTopicCreated != null -> { /* topic created */ }
                message.forumTopicClosed != null -> { /* topic closed */ }
                message.forumTopicReopened != null -> { /* topic reopened */ }
                message.forumTopicEdited != null -> { /* topic renamed or re-iconed */ }
                message.generalForumTopicHidden != null -> { /* General topic hidden */ }
                message.generalForumTopicUnhidden != null -> { /* General topic unhidden */ }
            }
        }
    }
}
```

For more information about forum topics check the official documentation at https://core.telegram.org/bots/api#forum-topics.
