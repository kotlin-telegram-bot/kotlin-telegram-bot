package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.google.gson.annotations.SerializedName

/**
 * This object represents an inline keyboard that appears right next to the message it belongs to.
 * @param inlineKeyboard Array of button rows, each represented by an Array of [InlineKeyboardButton] objects.
 * @see <https://core.telegram.org/bots/api#inlinekeyboardmarkup>
 */
public data class InlineKeyboardMarkup internal constructor(
    @SerializedName("inline_keyboard") val inlineKeyboard: List<List<InlineKeyboardButton>>
) : ReplyMarkup {

    init {
        validatePriorityButtonsForType<InlineKeyboardButton.Pay>()
        validatePriorityButtonsForType<InlineKeyboardButton.CallbackGameButtonType>()
    }

    // Priority buttons must always be the first button in the first row.
    private inline fun <reified T> validatePriorityButtonsForType() {
        val flattenedButtons = inlineKeyboard.flatten()
        val filteredButtons = flattenedButtons.filterIsInstance<T>()
        val typeName = T::class.simpleName

        require(filteredButtons.size <= 1) {
            "Can't have more than one button of type $typeName per inline keyboard"
        }

        require(filteredButtons.size != 1 || flattenedButtons.firstOrNull() is T) {
            "Buttons of type $typeName must always be the first button in the first row"
        }
    }

    public companion object {
        private val GSON = GsonFactory.createForApiClient()

        public fun createSingleButton(button: InlineKeyboardButton): InlineKeyboardMarkup = InlineKeyboardMarkup(listOf(listOf(button)))
        public fun createSingleRowKeyboard(buttons: List<InlineKeyboardButton>): InlineKeyboardMarkup = InlineKeyboardMarkup(listOf(buttons))
        public fun createSingleRowKeyboard(vararg button: InlineKeyboardButton): InlineKeyboardMarkup = InlineKeyboardMarkup(listOf(button.toList()))
        public fun create(buttons: List<List<InlineKeyboardButton>>): InlineKeyboardMarkup = InlineKeyboardMarkup(buttons)
        public fun create(vararg buttonsRow: List<InlineKeyboardButton>): InlineKeyboardMarkup = InlineKeyboardMarkup(buttonsRow.toList())
    }

    override fun toString(): String = GSON.toJson(this)
}
