# File uploads

Every operation that uploads a media file in this library accepts the same sealed `TelegramFile` type, so you never have to deal with multipart forms by hand. There are five flavours: `ByFileId` reuses a file already on Telegram's servers, `ByUrl` asks Telegram to fetch a remote URL, `ByFile` reads a local `java.io.File`, `ByByteArray` uploads an in-memory blob, and `ByInputStream` (added in Bot API 7.x) streams the file bytes — handy when you are proxying a download and don't want to buffer it all in memory.

```kotlin
import com.github.kotlintelegrambot.entities.TelegramFile
import okhttp3.MediaType.Companion.toMediaType
import java.io.File

val byFileId = TelegramFile.ByFileId("AgACAgIAAxkBAAEC...")
val byUrl = TelegramFile.ByUrl("https://example.com/photo.jpg")
val byFile = TelegramFile.ByFile(File("/path/photo.jpg"))
val byBytes = TelegramFile.ByByteArray(fileBytes = photoBytes, filename = "photo.jpg")
val byStream = TelegramFile.ByInputStream(
    stream = httpResponse.body!!.byteStream(),
    filename = "photo.jpg",
    contentType = "image/jpeg".toMediaType(),
)
```

Every flavour goes through the same `sendPhoto` (or `sendAudio`, `sendVideo`, ...) call:

```kotlin
val bot = bot { token = BOT_API_TOKEN }
val chatId = ChatId.fromId(USER_CHAT_ID)

bot.sendPhoto(chatId, photo = byFileId)
bot.sendPhoto(chatId, photo = byUrl)
bot.sendPhoto(chatId, photo = byFile)
bot.sendPhoto(chatId, photo = byBytes, caption = "in-memory blob")
bot.sendPhoto(chatId, photo = byStream, caption = "streamed upload")
```

The same `TelegramFile` flavours work with `sendAudio`, `sendVideo`, `sendDocument`, `sendSticker`, `sendVideoNote`, `sendVoice` and `sendAnimation`, as well as with the items you pass to `sendMediaGroup`.

A small but important detail: when you build a `sendMediaGroup` out of `TelegramFile.ByByteArray` parts, give each part a **unique filename**. Telegram references the attached parts by filename in the multipart body, so two parts called `photo.jpg` will collide and the second one will silently overwrite the first.

For all the details about which formats and size limits Telegram accepts for each kind of file, see the official docs:

* https://core.telegram.org/bots/api#sending-files
