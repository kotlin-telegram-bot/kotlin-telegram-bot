package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName as Name

/**
 * Color scheme for a user's name, message replies and link previews, derived from a unique gift.
 * (Bot API 9.3)
 *
 * https://core.telegram.org/bots/api#uniquegiftcolors
 *
 * Colors are stored as RGB integers (0xRRGGBB).
 */
data class UniqueGiftColors(
    @Name("model_custom_emoji_id") val modelCustomEmojiId: String,
    @Name("symbol_custom_emoji_id") val symbolCustomEmojiId: String,
    @Name("light_theme_main_color") val lightThemeMainColor: Int,
    @Name("light_theme_other_colors") val lightThemeOtherColors: List<Int>,
    @Name("dark_theme_main_color") val darkThemeMainColor: Int,
    @Name("dark_theme_other_colors") val darkThemeOtherColors: List<Int>,
)
