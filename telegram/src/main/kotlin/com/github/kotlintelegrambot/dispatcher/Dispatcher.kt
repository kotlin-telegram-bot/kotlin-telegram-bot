package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.ContactHandleUpdate
import com.github.kotlintelegrambot.HandleAnimationUpdate
import com.github.kotlintelegrambot.HandleAudioUpdate
import com.github.kotlintelegrambot.HandleDice
import com.github.kotlintelegrambot.HandleDocumentUpdate
import com.github.kotlintelegrambot.HandleError
import com.github.kotlintelegrambot.HandleGameUpdate
import com.github.kotlintelegrambot.HandleInlineQuery
import com.github.kotlintelegrambot.HandleNewChatMembers
import com.github.kotlintelegrambot.HandlePhotosUpdate
import com.github.kotlintelegrambot.HandlePollAnswer
import com.github.kotlintelegrambot.HandleStickerUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.HandleVideoNoteUpdate
import com.github.kotlintelegrambot.HandleVideoUpdate
import com.github.kotlintelegrambot.HandleVoiceUpdate
import com.github.kotlintelegrambot.LocationHandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ChannelHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CheckoutHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.ContactHandler
import com.github.kotlintelegrambot.dispatcher.handlers.DiceHandler
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.handlers.InlineQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.LocationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandler
import com.github.kotlintelegrambot.dispatcher.handlers.NewChatMembersHandler
import com.github.kotlintelegrambot.dispatcher.handlers.PollAnswerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AnimationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AudioHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.DocumentHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.GameHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.PhotosHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.StickerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoNoteHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VoiceHandler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.TelegramError
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.DispatchableObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

fun Dispatcher.message(handleUpdate: HandleUpdate) {
    addHandler(MessageHandler(handleUpdate, Filter.All))
}

fun Dispatcher.message(filter: Filter, handleUpdate: HandleUpdate) {
    addHandler(MessageHandler(handleUpdate, filter))
}

fun Dispatcher.command(command: String, body: CommandHandlerEnvironment.() -> Unit) {
    addHandler(CommandHandler(command, body))
}

fun Dispatcher.text(text: String? = null, body: HandleUpdate) {
    addHandler(TextHandler(text, body))
}

fun Dispatcher.callbackQuery(data: String? = null, body: HandleUpdate) {
    addHandler(CallbackQueryHandler(callbackData = data, handler = body))
}

fun Dispatcher.callbackQuery(callbackQueryHandler: CallbackQueryHandler) {
    addHandler(callbackQueryHandler)
}

fun Dispatcher.contact(handleUpdate: ContactHandleUpdate) {
    addHandler(ContactHandler(handleUpdate))
}

fun Dispatcher.location(handleUpdate: LocationHandleUpdate) {
    addHandler(LocationHandler(handleUpdate))
}

fun Dispatcher.telegramError(body: HandleError) {
    addErrorHandler(body)
}

fun Dispatcher.preCheckoutQuery(body: HandleUpdate) {
    addHandler(CheckoutHandler(body))
}

fun Dispatcher.channel(body: HandleUpdate) {
    addHandler(ChannelHandler(body))
}

fun Dispatcher.inlineQuery(body: HandleInlineQuery) {
    addHandler(InlineQueryHandler(body))
}

fun Dispatcher.audio(body: HandleAudioUpdate) {
    addHandler(AudioHandler(body))
}

fun Dispatcher.document(body: HandleDocumentUpdate) {
    addHandler(DocumentHandler(body))
}

fun Dispatcher.animation(body: HandleAnimationUpdate) {
    addHandler(AnimationHandler(body))
}

fun Dispatcher.game(body: HandleGameUpdate) {
    addHandler(GameHandler(body))
}

fun Dispatcher.photos(body: HandlePhotosUpdate) {
    addHandler(PhotosHandler(body))
}

fun Dispatcher.sticker(body: HandleStickerUpdate) {
    addHandler(StickerHandler(body))
}

fun Dispatcher.video(body: HandleVideoUpdate) {
    addHandler(VideoHandler(body))
}

fun Dispatcher.voice(body: HandleVoiceUpdate) {
    addHandler(VoiceHandler(body))
}

fun Dispatcher.videoNote(body: HandleVideoNoteUpdate) {
    addHandler(VideoNoteHandler(body))
}

fun Dispatcher.newChatMembers(body: HandleNewChatMembers) {
    addHandler(NewChatMembersHandler(body))
}

fun Dispatcher.pollAnswer(body: HandlePollAnswer) {
    addHandler(PollAnswerHandler(body))
}

fun Dispatcher.dice(body: HandleDice) {
    addHandler(DiceHandler(body))
}

class Dispatcher(
    val updatesQueue: BlockingQueue<DispatchableObject> = LinkedBlockingQueue()
) {
    internal lateinit var logLevel: LogLevel
    lateinit var bot: Bot

    private val commandHandlers = mutableMapOf<String, ArrayList<Handler>>()
    private val errorHandlers = arrayListOf<HandleError>()
    private var stopped = false

    fun startCheckingUpdates() {
        stopped = false
        checkQueueUpdates()
    }

    private fun checkQueueUpdates() {
        while (!Thread.currentThread().isInterrupted && !stopped) {
            val item = updatesQueue.take()
            when (item) {
                is Update -> handleUpdate(item)
                is TelegramError -> handleError(item)
                else -> Unit
            }
        }
    }

    fun addHandler(handler: Handler) {
        var handlers = commandHandlers[handler.groupIdentifier]

        if (handlers == null) {
            handlers = arrayListOf()
            commandHandlers[handler.groupIdentifier] = handlers
        }

        handlers.add(handler)
    }

    fun removeHandler(handler: Handler) {
        commandHandlers[handler.groupIdentifier]?.remove(handler)
    }

    fun addErrorHandler(errorHandler: HandleError) {
        errorHandlers.add(errorHandler)
    }

    fun removeErrorHandler(errorHandler: HandleError) {
        errorHandlers.remove(errorHandler)
    }

    private fun handleUpdate(update: Update) {
        for (group in commandHandlers) {
            group.value
                .filter { it.checkUpdate(update) }
                .forEach {
                    try {
                        it.handlerCallback(bot, update)
                    } catch (exc: Exception) {
                        if (logLevel.shouldLogErrors()) {
                            exc.printStackTrace()
                        }
                    }
                }
        }
    }

    private fun handleError(error: TelegramError) {
        errorHandlers.forEach {
            try {
                it(bot, error)
            } catch (exc: Exception) {
                if (logLevel.shouldLogErrors()) {
                    exc.printStackTrace()
                }
            }
        }
    }

    internal fun stopCheckingUpdates() {
        stopped = true
    }
}
