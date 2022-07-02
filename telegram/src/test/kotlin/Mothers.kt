import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.Contact
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.InlineQuery
import com.github.kotlintelegrambot.entities.Invoice
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.Venue
import com.github.kotlintelegrambot.entities.dice.Dice
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.files.Animation
import com.github.kotlintelegrambot.entities.files.Audio
import com.github.kotlintelegrambot.entities.files.ChatPhoto
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.entities.files.Video
import com.github.kotlintelegrambot.entities.files.VideoNote
import com.github.kotlintelegrambot.entities.files.Voice
import com.github.kotlintelegrambot.entities.payments.OrderInfo
import com.github.kotlintelegrambot.entities.payments.PreCheckoutQuery
import com.github.kotlintelegrambot.entities.payments.ShippingQuery
import com.github.kotlintelegrambot.entities.payments.SuccessfulPayment
import com.github.kotlintelegrambot.entities.polls.PollAnswer
import com.github.kotlintelegrambot.entities.stickers.Sticker
import java.math.BigInteger

private const val ANY_UPDATE_ID = 3523523L

fun anyUpdate(
    updateId: Long = ANY_UPDATE_ID,
    message: Message? = null,
    editedMessage: Message? = null,
    channelPost: Message? = null,
    editedChannelPost: Message? = null,
    callbackQuery: CallbackQuery? = null,
    preCheckoutQuery: PreCheckoutQuery? = null,
    shippingQuery: ShippingQuery? = null,
    inlineQuery: InlineQuery? = null,
    pollAnswer: PollAnswer? = null
): Update = Update(
    updateId = updateId,
    message = message,
    editedMessage = editedMessage,
    channelPost = channelPost,
    editedChannelPost = editedChannelPost,
    callbackQuery = callbackQuery,
    preCheckoutQuery = preCheckoutQuery,
    shippingQuery = shippingQuery,
    inlineQuery = inlineQuery,
    pollAnswer = pollAnswer
)

private const val ANY_MESSAGE_ID = 32142353L
private const val ANY_DATE = 12412421L

fun anyMessage(
    messageId: Long = ANY_MESSAGE_ID,
    from: User? = null,
    date: Long = ANY_DATE,
    chat: Chat = anyChat(),
    forwardFrom: User? = null,
    forwardFromChat: Chat? = null,
    forwardDate: Int? = null,
    replyToMessage: Message? = null,
    editDate: Int? = null,
    text: String? = null,
    entities: List<MessageEntity>? = null,
    captionEntities: List<MessageEntity>? = null,
    audio: Audio? = null,
    document: Document? = null,
    game: Game? = null,
    photo: List<PhotoSize>? = null,
    sticker: Sticker? = null,
    video: Video? = null,
    voice: Voice? = null,
    videoNote: VideoNote? = null,
    caption: String? = null,
    contact: Contact? = null,
    location: Location? = null,
    venue: Venue? = null,
    newChatMembers: List<User>? = null,
    leftChatMember: User? = null,
    newChatTitle: String? = null,
    newChatPhoto: List<PhotoSize>? = null,
    deleteChatPhoto: Boolean? = null,
    groupChatCreated: Boolean? = null,
    supergroupChatCreated: Boolean? = null,
    channelChatCreated: Boolean? = null,
    migrateToChatId: Long? = null,
    migrateFromChatId: Long? = null,
    invoice: Invoice? = null,
    successfulPayment: SuccessfulPayment? = null,
    dice: Dice? = null,
    animation: Animation? = null,
    replyMarkup: InlineKeyboardMarkup? = null
): Message = Message(
    messageId = messageId,
    from = from,
    date = date,
    chat = chat,
    forwardFrom = forwardFrom,
    forwardFromChat = forwardFromChat,
    forwardDate = forwardDate,
    replyToMessage = replyToMessage,
    editDate = editDate,
    text = text,
    entities = entities,
    captionEntities = captionEntities,
    audio = audio,
    document = document,
    game = game,
    photo = photo,
    sticker = sticker,
    video = video,
    voice = voice,
    videoNote = videoNote,
    caption = caption,
    contact = contact,
    location = location,
    venue = venue,
    newChatMembers = newChatMembers,
    leftChatMember = leftChatMember,
    newChatTitle = newChatTitle,
    newChatPhoto = newChatPhoto,
    deleteChatPhoto = deleteChatPhoto,
    groupChatCreated = groupChatCreated,
    supergroupChatCreated = supergroupChatCreated,
    channelChatCreated = channelChatCreated,
    migrateToChatId = migrateToChatId,
    migrateFromChatId = migrateFromChatId,
    invoice = invoice,
    successfulPayment = successfulPayment,
    dice = dice,
    animation = animation,
    replyMarkup = replyMarkup
)

private const val ANY_CHAT_ID = 243423535L
private const val ANY_CHAT_TYPE = "private"

fun anyChat(
    id: Long = ANY_CHAT_ID,
    type: String = ANY_CHAT_TYPE,
    title: String? = null,
    username: String? = null,
    firstName: String? = null,
    lastName: String? = null,
    photo: ChatPhoto? = null,
    description: String? = null,
    inviteLink: String? = null,
    pinnedMessage: String? = null,
    stickerSetName: String? = null,
    canSetStickerSet: Boolean? = null
): Chat = Chat(
    id = id,
    type = type,
    title = title,
    username = username,
    firstName = firstName,
    lastName = lastName,
    photo = photo,
    description = description,
    inviteLink = inviteLink,
    pinnedMessage = pinnedMessage,
    stickerSetName = stickerSetName,
    canSetStickerSet = canSetStickerSet
)

private const val ANY_FILE_ID = "fileId:353432q213412sd"
private const val ANY_FILE_UNIQUE_ID = "fileUniqueId:3513523frj2"
private const val ANY_DURATION = 2421432

fun anyAudio(
    fileId: String = ANY_FILE_ID,
    fileUniqueId: String = ANY_FILE_UNIQUE_ID,
    duration: Int = ANY_DURATION,
    performer: String? = null,
    title: String? = null,
    mimeType: String? = null,
    fileSize: Int? = null,
    fileName: String? = null
): Audio = Audio(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    duration = duration,
    performer = performer,
    title = title,
    mimeType = mimeType,
    fileSize = fileSize,
    fileName = fileName
)

private const val ANY_WIDTH = 23452345
private const val ANY_HEIGHT = 674654

fun anyPhotoSize(
    fileId: String = ANY_FILE_ID,
    fileUniqueId: String = ANY_FILE_UNIQUE_ID,
    width: Int = ANY_WIDTH,
    height: Int = ANY_HEIGHT,
    fileSize: Int? = null
): PhotoSize = PhotoSize(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    width = width,
    height = height,
    fileSize = fileSize
)

fun anySticker(
    fileId: String = ANY_FILE_ID,
    fileUniqueId: String = ANY_FILE_UNIQUE_ID,
    width: Int = ANY_WIDTH,
    height: Int = ANY_HEIGHT,
    isAnimated: Boolean = false,
    thumb: PhotoSize? = null,
    emoji: String? = null,
    fileSize: Int? = null
): Sticker = Sticker(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    width = width,
    height = height,
    isAnimated = isAnimated,
    thumb = thumb,
    emoji = emoji,
    fileSize = fileSize
)

fun anyVideo(
    fileId: String = ANY_FILE_ID,
    fileUniqueId: String = ANY_FILE_UNIQUE_ID,
    width: Int = ANY_WIDTH,
    height: Int = ANY_HEIGHT,
    thumb: PhotoSize? = null,
    mimeType: String? = null,
    fileSize: Int? = null,
    duration: Int = ANY_DURATION,
    fileName: String? = null
): Video = Video(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    width = width,
    height = height,
    thumb = thumb,
    mimeType = mimeType,
    fileSize = fileSize,
    duration = duration,
    fileName = fileName
)

private const val ANY_LENGTH = 234234

fun anyVideoNote(
    fileId: String = ANY_FILE_ID,
    fileUniqueId: String = ANY_FILE_UNIQUE_ID,
    thumb: PhotoSize? = null,
    fileSize: Int? = null,
    duration: Int = ANY_DURATION,
    length: Int = ANY_LENGTH
): VideoNote = VideoNote(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    thumb = thumb,
    fileSize = fileSize,
    duration = duration,
    length = length
)

private const val ANY_LONGITUDE = 3.2235423F
private const val ANY_LATITUDE = 32.1242F
private const val ANY_LIVE_PERIOD = 23

fun anyLocation(
    longitude: Float = ANY_LONGITUDE,
    latitude: Float = ANY_LATITUDE,
    livePeriod: Int? = ANY_LIVE_PERIOD,
): Location = Location(
    longitude = longitude,
    latitude = latitude,
    livePeriod = livePeriod
)

private const val ANY_PHONE_NUMBER = "+346878344312"
private const val ANY_FIRST_NAME = "rukeitor"

fun anyContact(
    phoneNumber: String = ANY_PHONE_NUMBER,
    firstName: String = ANY_FIRST_NAME,
    lastName: String? = null,
    userId: Long? = null
): Contact = Contact(
    phoneNumber = phoneNumber,
    firstName = firstName,
    lastName = lastName,
    userId = userId
)

private const val ANY_TITLE = "invoiceando"
private const val ANY_DESCRIPTION = "describiendo"
private const val ANY_START_PARAMETER = "ruka"
private const val ANY_CURRENCY = "€"
private const val ANY_TOTAL_AMOUNT = 666

fun anyInvoice(
    title: String = ANY_TITLE,
    description: String = ANY_DESCRIPTION,
    startParameter: String = ANY_START_PARAMETER,
    currency: String = ANY_CURRENCY,
    totalAmount: Int = ANY_TOTAL_AMOUNT
): Invoice = Invoice(
    title = title,
    description = description,
    startParameter = startParameter,
    currency = currency,
    totalAmount = totalAmount
)

private const val ANY_USER_ID = 325235L
private const val IS_BOT = true

fun anyUser(
    userId: Long = ANY_USER_ID,
    isBot: Boolean = IS_BOT,
    firstName: String = ANY_FIRST_NAME,
    lastName: String? = null,
    username: String? = null,
    languageCode: String? = null
): User = User(
    id = userId,
    isBot = isBot,
    firstName = firstName,
    lastName = lastName,
    username = username,
    languageCode = languageCode
)

fun anyCallbackQuery(
    id: String = "anyId",
    from: User = anyUser(),
    message: Message? = null,
    inlineMessageId: String? = null,
    data: String = "anyData",
    chatInstance: String = "1"
): CallbackQuery = CallbackQuery(
    id = id,
    from = from,
    message = message,
    inlineMessageId = inlineMessageId,
    data = data,
    chatInstance = chatInstance
)

fun anyInlineQuery(
    id: String = "anyId",
    from: User = anyUser(),
    location: Location? = null,
    query: String = "anyQuery",
    offset: String = "anyOffset"
): InlineQuery = InlineQuery(
    id = id,
    from = from,
    location = location,
    query = query,
    offset = offset
)

fun anyPollAnswer(
    pollId: String = "anyPollId",
    user: User = anyUser(),
    optionIds: List<Int> = emptyList()
): PollAnswer = PollAnswer(
    pollId = pollId,
    user = user,
    optionIds = optionIds
)

fun anyDice(
    diceEmoji: DiceEmoji = DiceEmoji.Dartboard,
    value: Int = 5
): Dice = Dice(
    emoji = diceEmoji,
    value = value
)

fun anyAnimation(
    fileId: String = "anyFileId",
    fileUniqueId: String = "anyFileUniqueId",
    width: Int = 200,
    height: Int = 244,
    duration: Int = 299,
    thumb: PhotoSize? = null,
    fileName: String? = null,
    mimeType: String? = null,
    fileSize: Long? = null
): Animation = Animation(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    width = width,
    height = height,
    duration = duration,
    thumb = thumb,
    fileName = fileName,
    mimeType = mimeType,
    fileSize = fileSize
)

fun anyDocument(
    fileId: String = "anyFileId",
    fileUniqueId: String = "anyFileUniqueId",
    thumb: PhotoSize? = null,
    fileName: String? = null,
    mimeType: String? = null,
    fileSize: Int? = null
): Document = Document(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    thumb = thumb,
    fileName = fileName,
    mimeType = mimeType,
    fileSize = fileSize
)

fun anyGame(
    title: String = "anyTitle",
    description: String = "anyDescription",
    photos: List<PhotoSize> = emptyList(),
    text: String? = null,
    textEntities: List<MessageEntity>? = null,
    animation: Animation? = null
): Game = Game(
    title = title,
    description = description,
    photo = photos,
    text = text,
    textEntities = textEntities,
    animation = animation
)

fun anyVoice(
    fileId: String = "anyFileId",
    fileUniqueId: String = "anyUniqueFileId",
    duration: Int = 14124,
    mimeType: String? = null,
    fileSize: Int? = null
): Voice = Voice(
    fileId = fileId,
    fileUniqueId = fileUniqueId,
    duration = duration,
    mimeType = mimeType,
    fileSize = fileSize
)

fun anyPreCheckoutQuery(
    id: String = "anyId",
    from: User = anyUser(),
    currency: String = "USD",
    totalAmount: BigInteger = BigInteger.ONE,
    invoicePayload: String = "anyInvoicePayload",
    shippingOptionId: String? = null,
    orderInfo: OrderInfo? = null
): PreCheckoutQuery = PreCheckoutQuery(
    id = id,
    from = from,
    currency = currency,
    totalAmount = totalAmount,
    invoicePayload = invoicePayload,
    shippingOptionId = shippingOptionId,
    orderInfo = orderInfo
)
