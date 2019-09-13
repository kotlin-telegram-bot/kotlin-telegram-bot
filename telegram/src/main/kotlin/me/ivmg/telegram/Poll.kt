package me.ivmg.telegram

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.entities.PollOption

data class Poll(
    val id: Long,
    val question: String,
    val options: List<PollOption>,
    @Name("is_closed") val isClosed: Boolean
)
