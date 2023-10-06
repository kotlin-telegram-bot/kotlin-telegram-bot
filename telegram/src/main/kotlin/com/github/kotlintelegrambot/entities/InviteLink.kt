package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class InviteLink(
    @SerializedName("invite_link")
    val inviteLink: String,
    @SerializedName("creator")
    val creator: User,
)
