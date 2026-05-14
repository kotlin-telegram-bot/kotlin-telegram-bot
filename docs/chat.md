# Chat and ChatFullInfo

Since Bot API 7.3 the Telegram chat object is modelled with **two distinct types**:

- **`Chat`** — the minimal representation that arrives nested inside `Message.chat`, `Update.chat_member.chat`, `Update.business_connection.user_chat_id`, etc. Fields: `id`, `type`, `title`, `username`, `firstName`, `lastName`, `isForum`, `isDirectMessages`.
- **`ChatFullInfo`** — the rich profile (`bio`, `description`, `photo`, `permissions`, accent colours, business intro / location / opening hours, gift settings, …). Returned by `getChat`.

Both live under `com.github.kotlintelegrambot.entities`.

## What you get nested

When you receive an update, the chat reference is always the minimal type:

```kotlin
dispatch {
    text {
        val chat: Chat = message.chat
        println(chat.id)
        println(chat.title ?: chat.firstName)
        // chat.bio  ← compile error, not on Chat
    }
}
```

If you need the bio, description, photo, permissions, etc., fetch the full profile explicitly:

```kotlin
val full: TelegramBotResult<ChatFullInfo> = bot.getChat(ChatId.fromId(chat.id))

full.fold(
    ifSuccess = { profile ->
        println("bio: ${profile.bio}")
        println("description: ${profile.description}")
        println("accentColorId: ${profile.accentColorId}")
        profile.businessIntro?.let { println("intro: ${it.title}") }
    },
    ifError = { /* ... */ },
)
```

## Notable `ChatFullInfo` fields

- Required on every successful response: `id`, `type`, `accentColorId`, `maxReactionCount`, `acceptedGiftTypes`.
- Optional, present per chat type:
  - **All chats**: `photo`, `permissions`, `inviteLink`, `pinnedMessage`.
  - **Private chats**: `bio`, `birthdate`, `hasPrivateForwards`, `hasRestrictedVoiceAndVideoMessages`, `personalChat`, `rating: UserRating?`, `firstProfileAudio: Audio?`, `paidMessageStarCount`.
  - **Groups / supergroups / channels**: `description`, `slowModeDelay`, `unrestrictBoostCount`, `messageAutoDeleteTime`, `hasProtectedContent`, `hasVisibleHistory`, `hasHiddenMembers`, `hasAggressiveAntiSpamEnabled`, `linkedChatId`, `location`, `stickerSetName`, `canSetStickerSet`, `customEmojiStickerSetName`, `joinToSendMessages`, `joinByRequest`, `availableReactions: List<ReactionType>?`.
  - **Business chats**: `businessIntro`, `businessLocation`, `businessOpeningHours`.
  - **Direct messages chats**: `parentChat: Chat?`.
  - **Accent / cosmetic**: `accentColorId`, `backgroundCustomEmojiId`, `profileAccentColorId`, `profileBackgroundCustomEmojiId`, `emojiStatusCustomEmojiId`, `emojiStatusExpirationDate`, `uniqueGiftColors`.
  - **Gifts**: `acceptedGiftTypes`, `canSendPaidMedia`.

## See also

- Telegram Bot API spec: [`Chat`](https://core.telegram.org/bots/api#chat), [`ChatFullInfo`](https://core.telegram.org/bots/api#chatfullinfo).
