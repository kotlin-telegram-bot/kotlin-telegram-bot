package com.github.kotlintelegrambot.network.serialization

import com.github.kotlintelegrambot.entities.BackgroundFill
import com.github.kotlintelegrambot.entities.BackgroundType
import com.github.kotlintelegrambot.entities.ChatBoostSource
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InputProfilePhoto
import com.github.kotlintelegrambot.entities.MenuButton
import com.github.kotlintelegrambot.entities.MessageOrigin
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import com.github.kotlintelegrambot.entities.gifts.OwnedGift
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inputmedia.GroupableMedia
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.payments.PaidMedia
import com.github.kotlintelegrambot.entities.payments.RevenueWithdrawalState
import com.github.kotlintelegrambot.entities.payments.TransactionPartner
import com.github.kotlintelegrambot.entities.polls.InputPollMedia
import com.github.kotlintelegrambot.entities.polls.InputPollOptionMedia
import com.github.kotlintelegrambot.entities.polls.PollMedia
import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.github.kotlintelegrambot.entities.stories.InputStoryContent
import com.github.kotlintelegrambot.entities.stories.StoryAreaType
import com.github.kotlintelegrambot.network.serialization.adapter.ChatIdAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.DiceEmojiAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.GroupableMediaAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.InlineKeyboardButtonAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.InlineQueryResultAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.InputMediaAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.PolymorphicTypeAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.ReactionTypeAdapter
import com.github.kotlintelegrambot.network.serialization.adapter.TelegramFileAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal object GsonFactory {

    fun createForApiClient(): Gson = GsonBuilder()
        // Custom shape adapters (not just polymorphic dispatch on a discriminator)
        .registerTypeAdapter(InlineQueryResult::class.java, InlineQueryResultAdapter())
        .registerTypeAdapter(InlineKeyboardButton::class.java, InlineKeyboardButtonAdapter())
        .registerTypeAdapter(DiceEmoji::class.java, DiceEmojiAdapter())
        .registerTypeAdapter(TelegramFile.ByFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(TelegramFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(InputMedia::class.java, InputMediaAdapter())
        .registerTypeAdapter(GroupableMedia::class.java, GroupableMediaAdapter(InputMediaAdapter()))
        .registerTypeAdapter(ReactionType::class.java, ReactionTypeAdapter())
        .registerTypeAdapter(ChatId.Id::class.java, ChatIdAdapter())
        .registerTypeAdapter(ChatId.ChannelUsername::class.java, ChatIdAdapter())
        .registerTypeAdapter(ChatId::class.java, ChatIdAdapter())
        // Polymorphic sealed types, all served by the same generic adapter
        .registerTypeAdapter(
            MessageOrigin::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "user" to MessageOrigin.User::class.java,
                    "hidden_user" to MessageOrigin.HiddenUser::class.java,
                    "chat" to MessageOrigin.Chat::class.java,
                    "channel" to MessageOrigin.Channel::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            ChatBoostSource::class.java,
            PolymorphicTypeAdapter(
                discriminator = "source",
                variants = mapOf(
                    "premium" to ChatBoostSource.Premium::class.java,
                    "gift_code" to ChatBoostSource.GiftCode::class.java,
                    "giveaway" to ChatBoostSource.Giveaway::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            BackgroundFill::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "solid" to BackgroundFill.Solid::class.java,
                    "gradient" to BackgroundFill.Gradient::class.java,
                    "freeform_gradient" to BackgroundFill.FreeformGradient::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            BackgroundType::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "fill" to BackgroundType.Fill::class.java,
                    "wallpaper" to BackgroundType.Wallpaper::class.java,
                    "pattern" to BackgroundType.Pattern::class.java,
                    "chat_theme" to BackgroundType.ChatTheme::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            TransactionPartner::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "user" to TransactionPartner.User::class.java,
                    "fragment" to TransactionPartner.Fragment::class.java,
                    "telegram_ads" to TransactionPartner.TelegramAds::class.java,
                    "telegram_api" to TransactionPartner.TelegramApi::class.java,
                    "affiliate_program" to TransactionPartner.AffiliateProgram::class.java,
                    "chat" to TransactionPartner.Chat::class.java,
                    "other" to TransactionPartner.Other::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            RevenueWithdrawalState::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "pending" to RevenueWithdrawalState.Pending::class.java,
                    "succeeded" to RevenueWithdrawalState.Succeeded::class.java,
                    "failed" to RevenueWithdrawalState.Failed::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            PaidMedia::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "preview" to PaidMedia.Preview::class.java,
                    "photo" to PaidMedia.Photo::class.java,
                    "video" to PaidMedia.Video::class.java,
                    "live_photo" to PaidMedia.LivePhoto::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            MenuButton::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "commands" to MenuButton.Commands::class.java,
                    "web_app" to MenuButton.WebApp::class.java,
                    "default" to MenuButton.Default::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            OwnedGift::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "regular" to OwnedGift.Regular::class.java,
                    "unique" to OwnedGift.Unique::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            InputProfilePhoto::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "static" to InputProfilePhoto.Static::class.java,
                    "animated" to InputProfilePhoto.Animated::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            InputStoryContent::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "photo" to InputStoryContent.Photo::class.java,
                    "video" to InputStoryContent.Video::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            StoryAreaType::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "location" to StoryAreaType.Location::class.java,
                    "suggested_reaction" to StoryAreaType.SuggestedReaction::class.java,
                    "link" to StoryAreaType.Link::class.java,
                    "weather" to StoryAreaType.Weather::class.java,
                    "unique_gift" to StoryAreaType.UniqueGift::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            PollMedia::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "photo" to PollMedia.Photo::class.java,
                    "video" to PollMedia.Video::class.java,
                    "live_photo" to PollMedia.LivePhoto::class.java,
                    "animation" to PollMedia.Animation::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            InputPollMedia::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "photo" to InputPollMedia.Photo::class.java,
                    "video" to InputPollMedia.Video::class.java,
                    "live_photo" to InputPollMedia.LivePhoto::class.java,
                    "animation" to InputPollMedia.Animation::class.java,
                ),
            ),
        )
        .registerTypeAdapter(
            InputPollOptionMedia::class.java,
            PolymorphicTypeAdapter(
                variants = mapOf(
                    "photo" to InputPollOptionMedia.Photo::class.java,
                    "video" to InputPollOptionMedia.Video::class.java,
                    "live_photo" to InputPollOptionMedia.LivePhoto::class.java,
                    "animation" to InputPollOptionMedia.Animation::class.java,
                ),
            ),
        )
        .create()

    fun createForMultipartBodyFactory(): Gson = GsonBuilder()
        .registerTypeAdapter(TelegramFile.ByFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(TelegramFile::class.java, TelegramFileAdapter())
        .registerTypeAdapter(GroupableMedia::class.java, GroupableMediaAdapter(InputMediaAdapter()))
        .create()
}
