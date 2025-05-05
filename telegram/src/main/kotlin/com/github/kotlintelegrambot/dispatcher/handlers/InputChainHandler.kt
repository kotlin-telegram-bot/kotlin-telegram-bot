package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

data class InputChainHandlerEnvironment<T>(
    val inputChainIdGen: (previousInputChainId: T?, Update) -> T?,
    val inputChainId: T,
    val bot: Bot,
    val update: Update,
    val message: Message,
)

class InputChainHandler<T>(
    private val filter: Filter,
    private val initialHandler: Handler,
    private val inputChainIdGen: (previousInputChainId: T?, Update) -> T?,
    private val handleInputChain: HandleInputChain<T>,
) : Handler {
    private val chainMap = mutableMapOf<Long, T?>()

    override fun checkUpdate(update: Update): Boolean =
        initialHandler.checkUpdate(update) || update.check()

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        update.message?.let { message ->
            message.from?.id?.let { userId ->
                chainMap.remove(userId)?.let { inputChainId ->
                    handleInputChain(
                        InputChainHandlerEnvironment(
                            inputChainIdGen,
                            inputChainId,
                            bot,
                            update,
                            message,
                        ),
                    )?.let { chainMap[userId] = it }
                    Unit
                }
            }
        } ?: initialHandler.handleUpdate(bot, update).also {
            update.takeIf { it.consumed }
                ?.userId()
                ?.let { chainMap[it] = inputChainIdGen(null, update) }
        }
    }

    private fun Update.check(): Boolean =
        message?.let(filter::checkFor) == true &&
            message.from?.id?.let(chainMap::get) != null

    private fun Update.userId(): Long? =
        message?.from?.id
            ?: callbackQuery?.from?.id
            ?: inlineQuery?.from?.id
            ?: channelPost?.from?.id
            ?: chosenInlineResult?.from?.id
            ?: shippingQuery?.from?.id
            ?: preCheckoutQuery?.from?.id
            ?: pollAnswer?.user?.id
            ?: editedMessage?.from?.id
            ?: editedChannelPost?.from?.id
}
