package com.github.kotlintelegrambot.network.serialization

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inputmedia.GroupableMedia
import com.github.kotlintelegrambot.network.serialization.adapter.DiceEmojiAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.GroupableMediaAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.InlineQueryResultAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.InputMediaAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.TelegramFileAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonFactory {

    fun createForApiClient(): Gson = GsonBuilder()
        .registerTypeAdapter(InlineQueryResult::class.java, InlineQueryResultAdapter())
        .registerTypeAdapter(DiceEmoji::class.java, DiceEmojiAdapter())
        .create()

    fun createForMultipartBodyFactory(): Gson = GsonBuilder()
        .registerTypeAdapter(TelegramFile.ByFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(TelegramFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(GroupableMedia::class.java, GroupableMediaAdapter(InputMediaAdapter()))
        .create()
}
