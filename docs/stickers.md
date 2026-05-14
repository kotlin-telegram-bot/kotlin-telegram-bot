# Stickers

You send a sticker through `bot.sendSticker`. The overload that takes a `String` is the simplest one when you're reusing a sticker that already lives on Telegram's servers — pass the `file_id` you received from a previous update or from `getStickerSet`. The `SystemFile` overload accepts a local `java.io.File` when you're uploading a fresh `.webp`, `.tgs` or `.webm`. The list of upload flavours used by other media methods (`TelegramFile.ByFileId`, `ByUrl`, `ByFile`, ...) is documented in [fileUploads.md](fileUploads.md); `sendSticker` itself only takes a file id or a system file.

```kotlin
import com.github.kotlintelegrambot.entities.ChatId

val chatId = ChatId.fromId(USER_CHAT_ID)

// Reuse a sticker already known to Telegram.
bot.sendSticker(chatId, sticker = "CAACAgIAAxkBAAEC...", replyMarkup = null)

// Upload a fresh sticker file from disk.
bot.sendSticker(chatId, sticker = java.io.File("/path/to/sticker.webp"), replyMarkup = null)
```

Listening for incoming stickers is just another handler:

```kotlin
bot {
    token = BOT_API_TOKEN
    dispatch {
        sticker {
            // sticker: Sticker — has fileId, emoji, setName, maskPosition, ...
        }
    }
}
```

To publish your own sticker pack, call `createNewStickerSet` with a user id, the pack's short `name` (must end in `_by_<bot_username>`), a human-readable `title`, the first sticker (`SystemFile` or file-id `String`), the list of emojis it should match, and — for mask packs — a `MaskPosition`. Add more stickers later with `addStickerToSet`, rearrange them with `setStickerPositionInSet`, remove individual stickers with `deleteStickerFromSet`, and inspect an existing set with `getStickerSet`. If you need to upload a sticker file separately before binding it to a set, use `uploadStickerFile`.

```kotlin
import com.github.kotlintelegrambot.entities.stickers.MaskPosition

bot.createNewStickerSet(
    userId = OWNER_USER_ID,
    name = "kittens_by_${botUsername}",
    title = "Kitten reactions",
    pngSticker = java.io.File("/path/sticker1.png"),
    emojis = "🐱", // 🐱
    containsMasks = false,
    maskPosition = null,
)

bot.addStickerToSet(
    userId = OWNER_USER_ID,
    name = "kittens_by_${botUsername}",
    pngSticker = "CAACAgIAAxkBAAEC...", // upload result file_id
    emojis = "😺",
    maskPosition = null,
)
```

The library also ships the `StickerFormat` (`STATIC`, `ANIMATED`, `VIDEO`) and `StickerType` (`REGULAR`, `MASK`, `CUSTOM_EMOJI`) enums used when deserializing stickers and `StickerSet` payloads from the API. The Stickers v2 management methods (`setStickerEmojiList`, `setStickerKeywords`, `setStickerMaskPosition`, `setStickerSetTitle`, `setStickerSetThumbnail`, `setCustomEmojiStickerSetThumbnail`, `deleteStickerSet`, `getCustomEmojiStickers`) are not yet bound on the `Bot` class.

Full specification: https://core.telegram.org/bots/api#stickers.
