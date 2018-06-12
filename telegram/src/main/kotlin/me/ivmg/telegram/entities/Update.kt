package me.ivmg.telegram.entities

import me.ivmg.telegram.entities.payment.PreCheckoutQuery
import me.ivmg.telegram.entities.payment.ShippingQuery
import me.ivmg.telegram.types.DispatchableObject
import com.google.gson.annotations.SerializedName as Name

data class Update constructor(
    @Name("update_id") val updateId: Long,
    val message: Message?,
    @Name("edited_message") val editedMessage: Message?,
    @Name("channel_post") val channelPost: Message?,
    @Name("edited_channel_post") val editedChannelPost: Message?,
    @Name("callback_query") val callbackQuery: CallbackQuery?,
    @Name("pre_checkout_query") val preCheckoutQuery: PreCheckoutQuery?,
    @Name("shipping_query") val shippingQuery: ShippingQuery?
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