package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.Poll
import me.ivmg.telegram.entities.payments.PreCheckoutQuery
import me.ivmg.telegram.entities.payments.ShippingQuery
import me.ivmg.telegram.types.DispatchableObject

data class Update constructor(
    @Name("update_id") val updateId: Long,
    val message: Message?,
    @Name("edited_message") val editedMessage: Message?,
    @Name("channel_post") val channelPost: Message?,
    @Name("edited_channel_post") val editedChannelPost: Message?,
    @Name("inline_query") val inlineQuery: InlineQuery?,
    @Name("chosen_inline_result") val chosenInlineResult: ChosenInlineResult?,
    @Name("callback_query") val callbackQuery: CallbackQuery?,
    @Name("shipping_query") val shippingQuery: ShippingQuery?,
    @Name("pre_checkout_query") val preCheckoutQuery: PreCheckoutQuery?,
    @Name("poll") val poll: Poll?
) : DispatchableObject

/**
 * Generate list of key-value from start payload.
 * For more info {@link https://core.telegram.org/bots#deep-linking}
 */
fun Update.getStartPayload(delimiter: String = "-"): List<Pair<String, String>> {
    return message?.let {
        val parameters = it.text?.substringAfter("start ", "")
        if (parameters == null || parameters.isEmpty()) {
            return emptyList()
        }

        val split = parameters.split("&")
        split.map {
            val keyValue = it.split(delimiter)
            Pair(keyValue[0], keyValue[1])
        }
    } ?: emptyList()
}
