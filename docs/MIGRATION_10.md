# Migrating from 6.x to 10.0

`10.0.0` brings the library up from Bot API 6.2 to Bot API 10.0 (24 versions of catch-up). It is a **breaking** release. The list below is the complete set of source-incompatible changes; everything else is additive and your code should keep compiling.

## Breaking — type renames / shape changes

### `getChat` now returns `ChatFullInfo`, not `Chat` (Bot API 7.3)

Telegram split the `Chat` object in two:

- **`Chat`** is the minimal nested representation that arrives inside `Message.chat`, `Update.chat_member.chat`, etc. Fields: `id`, `type`, `title`, `username`, `firstName`, `lastName`, `isForum`, `isDirectMessages`.
- **`ChatFullInfo`** is the rich profile (`bio`, `description`, `photo`, `permissions`, `accentColorId`, business intro/location/hours, etc.) returned by `getChat`.

```kotlin
// Before
val chat: TelegramBotResult<Chat> = bot.getChat(chatId)
println(chat.getOrNull()?.bio)            // worked

// After
val full: TelegramBotResult<ChatFullInfo> = bot.getChat(chatId)
println(full.getOrNull()?.bio)            // still works on ChatFullInfo

val msg: Message = updateMessage
println(msg.chat.bio)                     // COMPILE ERROR — Chat no longer has `bio`.
                                          // Fetch the full profile with bot.getChat(chat.id).
```

If you were reading rich fields off `Message.chat`/`Update.*.chat`, those reads always returned `null` from the server in nested positions — but the field existed. Now they're gone. Code that did `message.chat.bio.orEmpty()` must call `bot.getChat(message.chat.id)` to retrieve `ChatFullInfo`.

### `User.isPremium` moved to the end of the constructor

`isPremium: Boolean? = null` is now the last positional parameter on `User`. Construct `User` with named arguments (recommended in Kotlin anyway):

```kotlin
User(id = 1L, isBot = false, firstName = "Alice", isPremium = true)
```

### `Location` constructor field order

Added `horizontalAccuracy` and `heading` **at the end** of the constructor. Positional callers that passed `livePeriod`/`proximityAlertRadius` positionally still work; callers that already used named args are unaffected.

### Forward fields on `Message`

`forward_from`, `forward_from_chat`, `forward_from_message_id`, `forward_signature`, `forward_sender_name` and `forward_date` are still present for backward compat but Telegram no longer populates them — they will all be `null`. Use the new `Message.forwardOrigin: MessageOrigin?` instead:

```kotlin
when (val origin = message.forwardOrigin) {
    is MessageOrigin.User      -> origin.senderUser
    is MessageOrigin.HiddenUser -> origin.senderUserName
    is MessageOrigin.Chat      -> origin.senderChat
    is MessageOrigin.Channel   -> origin.chat to origin.messageId
    null -> /* not a forward */ Unit
}
```

### `MessageEntity.Type.EXPANDABLE_BLOCKQUOTE`

New enum constant for the Bot API 7.4 expandable blockquote. If you `when (entity.type) { ... }` on `MessageEntity.Type` exhaustively, the compiler will now require that branch.

### `ReactionType.Paid` sealed variant

New variant for Bot API 7.9 paid star reactions. Same caveat as above for exhaustive `when` on `ReactionType`.

### `ChatPermissions` — granular media permissions (Bot API 6.5)

The single `canSendMediaMessages` flag was split server-side into six granular flags (`canSendAudios`, `canSendDocuments`, `canSendPhotos`, `canSendVideos`, `canSendVideoNotes`, `canSendVoiceNotes`). The current code keeps the old flag for back-compat but the server may ignore it on supergroups — set the new ones if you target supergroup permission management.

### Removed duplicate types

The root-level duplicates `entities.SuccessfulPayment`, `entities.OrderInfo`, `entities.ShippingAddress` were deleted. The authoritative copies live under `entities.payments.*` and have additional 8.0 subscription fields. If you were importing the root-level versions, switch to `com.github.kotlintelegrambot.entities.payments.SuccessfulPayment` (etc.).

### Empty marker service messages are now `data object`

`ForumTopicClosed`, `ForumTopicReopened`, `GeneralForumTopicHidden`, `GeneralForumTopicUnhidden`, `ChatOwnerLeft` are `data object` instead of `class` with hand-rolled equals/hashCode. If you wrote `ForumTopicClosed()` (calling the constructor) it no longer compiles — drop the parentheses:

```kotlin
val closed: ForumTopicClosed = ForumTopicClosed
```

## New: cross-cutting parameters added to every `send*` / `edit*`

All canonical (non-deprecated) send and edit methods now accept these optional parameters at the end of their signatures. Defaulted to `null`, so existing calls keep compiling.

| Param | Type | Bot API | Applies to |
|---|---|---|---|
| `replyParameters` | `ReplyParameters?` | 7.0 | every `send*` and `copyMessage` |
| `linkPreviewOptions` | `LinkPreviewOptions?` | 7.0 | `sendMessage`, `editMessageText` |
| `businessConnectionId` | `String?` | 7.2 | every `send*`, `copyMessage`, every `edit*`, `sendChatAction` |
| `messageEffectId` | `String?` | 7.4 | every `send*` and `copyMessage` |
| `showCaptionAboveMedia` | `Boolean?` | 7.4 | media `send*`, `editMessageCaption`, `editMessageMedia`, `copyMessage` |
| `allowPaidBroadcast` | `Boolean?` | 7.11 | every `send*` and `copyMessage` |
| `messageThreadId` | `Long?` | 6.3 | every `send*`, `sendChatAction`, `unpinAllForumTopicMessages` |

`ReplyParameters` supersedes the legacy `replyToMessageId` + `allowSendingWithoutReply` pair; `LinkPreviewOptions` supersedes `disableWebPagePreview`. The legacy params still exist and still work — the new ones are richer (cross-chat replies, quoting, link-preview customisation). Prefer the new ones for new code.

## New: cross-cutting `Update` fields

You can now receive these on `Update` (all `null` if you didn't subscribe via `allowedUpdates`):

- `messageReaction: MessageReactionUpdated?` (7.0)
- `messageReactionCount: MessageReactionCountUpdated?` (7.0)
- `chatBoost: ChatBoostUpdated?` (7.0)
- `removedChatBoost: ChatBoostRemoved?` (7.0)
- `businessConnection: BusinessConnection?` (7.2)
- `businessMessage` / `editedBusinessMessage: Message?` (7.2)
- `deletedBusinessMessages: BusinessMessagesDeleted?` (7.2)
- `purchasedPaidMedia: PaidMediaPurchased?` (7.10)

## New: 100+ new entities

Tracked separately in their own docs (`docs/forumTopics.md`, `docs/businessConnection.md`, `docs/telegramStars.md`, `docs/paidMedia.md`, `docs/gifts.md`, `docs/stories.md`, `docs/reactions.md`). Highlights:

- Forum topics: `ForumTopic`, `ForumTopicCreated`, `ForumTopicEdited`, `WriteAccessAllowed`.
- Reactions: `MessageReactionUpdated`, `MessageReactionCountUpdated`, `ReactionType.Paid`.
- Boosts: `ChatBoost`, `ChatBoostSource` (sealed), `ChatBoostUpdated`, `ChatBoostRemoved`, `UserChatBoosts`, `ChatBoostAdded`.
- Giveaways: `Giveaway`, `GiveawayCreated`, `GiveawayWinners`, `GiveawayCompleted`.
- Business: `BusinessConnection`, `BusinessBotRights`, `BusinessIntro`, `BusinessLocation`, `BusinessOpeningHours`.
- Stars / payments: `RefundedPayment`, `StarTransaction`, `StarTransactions`, `StarAmount`, `RevenueWithdrawalState`, sealed `TransactionPartner` (7 variants), `AffiliateInfo`.
- Paid media: `PaidMedia` (sealed: Preview/Photo/Video/LivePhoto), `PaidMediaInfo`, `InputPaidMedia`, `PaidMediaPurchased`.
- Gifts: `Gift`, `Gifts`, `UniqueGift`, `OwnedGift` (sealed: Regular/Unique), `GiftInfo`, `UniqueGiftInfo`, `GiftBackground`, `UniqueGiftColors`, `AcceptedGiftTypes`.
- Stories: `InputStoryContent` (sealed), `StoryArea`, `StoryAreaType` (sealed: Location/SuggestedReaction/Link/Weather/UniqueGift), `LocationAddress`.
- Checklists: `Checklist`, `ChecklistTask`, `InputChecklist`, `InputChecklistTask`, `ChecklistTasksDone`, `ChecklistTasksAdded`.
- Suggested posts: `DirectMessagesTopic`, `SuggestedPostParameters`, `SuggestedPostInfo`, `SuggestedPostApproved`, `SuggestedPostApprovalFailed`, `SuggestedPostDeclined`, `SuggestedPostPaid`, `SuggestedPostRefunded`.
- 10.0: `LivePhoto`, sealed `PollMedia`/`InputPollMedia`/`InputPollOptionMedia`, `SentGuestMessage`, `BotAccessSettings`, `InputMedia{Sticker,Location,Venue,LivePhoto}`.

## New: ~50 new methods on `Bot`

Categories:
- Batch operations: `forwardMessages`, `copyMessages`, `deleteMessages`.
- Forum topics: `createForumTopic`, `editForumTopic`, `closeForumTopic`, `reopenForumTopic`, `deleteForumTopic`, `unpinAllForumTopicMessages`, `getForumTopicIconStickers`, `editGeneralForumTopic`, `closeGeneralForumTopic`, `reopenGeneralForumTopic`, `hideGeneralForumTopic`, `unhideGeneralForumTopic`.
- Bot info: `setMyDescription` / `getMyDescription` / `setMyShortDescription` / `getMyShortDescription` / `setMyName` / `getMyName` / `setChatMenuButton` / `getChatMenuButton` / `setMyDefaultAdministratorRights` / `getMyDefaultAdministratorRights`.
- Stars + paid media: `refundStarPayment`, `getStarTransactions`, `sendPaidMedia`, `getMyStarBalance`.
- Gifts: `getAvailableGifts`, `sendGift`, `giftPremiumSubscription`, `setUserEmojiStatus`, `savePreparedInlineMessage`.
- Verification: `verifyUser`, `verifyChat`, `removeUserVerification`, `removeChatVerification`.
- Business account mgmt: `readBusinessMessage`, `deleteBusinessMessages`, `setBusinessAccount{Name,Username,Bio,ProfilePhoto,GiftSettings}`, `removeBusinessAccountProfilePhoto`, `getBusinessAccountStarBalance`, `transferBusinessAccountStars`, `getBusinessAccountGifts`, `convertGiftToStars`, `upgradeGift`, `transferGift`, `postStory`, `editStory`, `deleteStory`.
- Reactions: `setMessageReaction`, `deleteMessageReaction`, `deleteAllMessageReactions`.
- Checklists: `sendChecklist`, `editMessageChecklist`.
- Suggested posts: `approveSuggestedPost`, `declineSuggestedPost`.
- Boosts: `getUserChatBoosts`.
- Guest mode (10.0): `answerGuestQuery`.

## Internal cleanup that may affect you

- ktlint runs on JDK 17+ in CI and locally (the build was bumped to require Java 17). The published library is still JVM 1.8 compatible.
- Kotlin compiler 2.3.x. If your project uses an older Kotlin compiler, you can still consume this library because we don't expose any Kotlin 2-specific public APIs.
- `kotlinx-coroutines` 1.11.0, `mockk` 1.14.9, `junit` 5.12.2, `ktor` 2.3.12 (samples only). Bump if you share these as runtime deps.

## Out of scope for this release (future follow-ups)

- Polls v3 (Bot API 9.6) — entities are present but `sendPoll` does not yet accept the new fields.
- Managed bots (Bot API 9.6) — entities present, methods pending.
- `Message.pinnedMessage` is still typed `Message?` rather than the spec's `MaybeInaccessibleMessage` union. Accessible messages deserialize correctly; inaccessible ones (where `date == 0`) parse but lose the explicit signalling.
- The legacy `replyToMessageId` / `allowSendingWithoutReply` / `disableWebPagePreview` parameters are NOT `@Deprecated` yet. They will be in the next release.
