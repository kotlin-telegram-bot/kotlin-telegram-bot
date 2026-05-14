# Checklists

Since Bot API 9.1 bots that act on behalf of a connected business account can send and edit interactive checklists. This library exposes the two operations directly on the `Bot` instance: `sendChecklist` and `editMessageChecklist`. Both require a `businessConnectionId`, so the bot must already be linked to a Telegram Business account.

To send a checklist, build an `InputChecklist` with one or more `InputChecklistTask` entries. Each task needs a unique `id` (an `Int`) and the text that will be shown to participants. The `othersCanAddTasks` and `othersCanMarkTasksAsDone` flags let you control whether other chat members may extend the list or tick items off:

```kotlin
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.checklists.InputChecklist
import com.github.kotlintelegrambot.entities.checklists.InputChecklistTask

bot.sendChecklist(
    businessConnectionId = "biz-connection-id",
    chatId = ChatId.fromId(CHAT_ID),
    checklist = InputChecklist(
        title = "Launch checklist",
        tasks = listOf(
            InputChecklistTask(id = 1, text = "Write release notes"),
            InputChecklistTask(id = 2, text = "Tag the build"),
            InputChecklistTask(id = 3, text = "Publish artifacts"),
        ),
        othersCanAddTasks = true,
        othersCanMarkTasksAsDone = true,
    ),
)
```

To replace the checklist attached to a previously sent message (for example, after the bot has crossed off some items programmatically), pass the same `businessConnectionId`, the original `messageId` and a fresh `InputChecklist`:

```kotlin
bot.editMessageChecklist(
    businessConnectionId = "biz-connection-id",
    chatId = ChatId.fromId(CHAT_ID),
    messageId = checklistMessageId,
    checklist = InputChecklist(
        title = "Launch checklist",
        tasks = listOf(
            InputChecklistTask(id = 1, text = "Write release notes"),
            InputChecklistTask(id = 2, text = "Tag the build"),
            InputChecklistTask(id = 3, text = "Publish artifacts"),
            InputChecklistTask(id = 4, text = "Announce in #general"),
        ),
    ),
)
```

On the receiving side a checklist arrives as a regular message: `message.checklist` (a `Checklist`) carries the current state of the list, while `message.checklistTasksDone` and `message.checklistTasksAdded` are service-message payloads describing the latest changes made by chat members.

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        message {
            message.checklist?.let { checklist -> /* full checklist state */ }
            message.checklistTasksDone?.let { done -> /* tasks just ticked off */ }
            message.checklistTasksAdded?.let { added -> /* tasks just appended */ }
        }
    }
}
```

For the full specification, see https://core.telegram.org/bots/api#sendchecklist.
