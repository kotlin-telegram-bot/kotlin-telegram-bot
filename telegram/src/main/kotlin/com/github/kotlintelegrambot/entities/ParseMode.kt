package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

// TODO: Remove modeName attribute and stop using it as a serialization approach for this enum
public enum class ParseMode(public val modeName: String) {
    @SerializedName("Markdown") MARKDOWN("Markdown"),
    @SerializedName("HTML") HTML("HTML"),
    @SerializedName("MarkdownV2") MARKDOWN_V2("MarkdownV2")
}
