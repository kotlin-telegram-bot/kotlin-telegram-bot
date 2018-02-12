package me.ivmg.telegram.entities

import me.ivmg.telegram.types.DispatchableObject
import com.google.gson.annotations.SerializedName as Name

data class Update(
    @Name("update_id") val updateId: Long,
    val message: Message?,
    @Name("edited_message") val editedMessage: Message?,
    @Name("channel_post") val channelPost: Message?,
    @Name("edited_channel_post") val editedChannelPost: Message?
) : DispatchableObject