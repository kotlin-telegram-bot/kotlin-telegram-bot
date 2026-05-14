# Stories

Business bots can post, edit and delete stories on behalf of a connected business account. The three operations are `bot.postStory`, `bot.editStory` and `bot.deleteStory`, and they all require the `businessConnectionId` received when the user connected their business account to the bot. The story body is described by an `InputStoryContent` (`Photo` or `Video`).

```kotlin
val bot = bot {
    token = BOT_API_TOKEN
    // additional configuration
}

val story = bot.postStory(
    businessConnectionId = BUSINESS_CONNECTION_ID,
    content = InputStoryContent.Photo(photo = "AgACAgIAAxk..."), // file_id
    activePeriod = 86400, // 24 hours
    caption = "Today",
    areas = listOf(
        StoryArea(
            position = StoryAreaPosition(
                xPercentage = 50f, yPercentage = 20f,
                widthPercentage = 30f, heightPercentage = 10f,
                rotationAngle = 0f, cornerRadiusPercentage = 10f,
            ),
            type = StoryAreaType.Location(latitude = 40.4168f, longitude = -3.7038f),
        ),
        StoryArea(
            position = StoryAreaPosition(50f, 50f, 40f, 10f, 0f, 10f),
            type = StoryAreaType.Link(url = "https://example.com"),
        ),
        StoryArea(
            position = StoryAreaPosition(80f, 80f, 20f, 10f, 0f, 10f),
            type = StoryAreaType.Weather(
                temperature = 22.0f,
                emoji = "sun",
                backgroundColor = 0xFF9900,
            ),
        ),
    ),
)
```

The `areas` parameter accepts a list of `StoryArea` overlays. Besides `Location`, `Link` and `Weather`, you can also use `SuggestedReaction` and `UniqueGift`. To modify a posted story you call `editStory` with its `storyId`, and to remove it you call `deleteStory`:

```kotlin
bot.editStory(
    businessConnectionId = BUSINESS_CONNECTION_ID,
    storyId = STORY_ID,
    content = InputStoryContent.Photo(photo = "AgACAgIAAxk..."),
    caption = "Updated",
)

bot.deleteStory(
    businessConnectionId = BUSINESS_CONNECTION_ID,
    storyId = STORY_ID,
)
```

Bots also receive stories that users share into a chat: `update.message.story` contains the shared `Story`, and `update.message.replyToStory` is populated when a message is sent as a reply to a story.

For more information about story management, see https://core.telegram.org/bots/api#poststory.
