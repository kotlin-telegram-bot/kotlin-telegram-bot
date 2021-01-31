package com.github.kotlintelegrambot.extensions.filters

import com.github.kotlintelegrambot.entities.Message

interface Filter {
    fun checkFor(message: Message): Boolean = message.predicate()
    fun Message.predicate(): Boolean

    infix fun and(otherFilter: Filter): Filter = object : Filter {
        override fun Message.predicate(): Boolean =
            this@Filter.checkFor(this) && otherFilter.checkFor(this)
    }

    infix fun or(otherFilter: Filter): Filter = object : Filter {
        override fun Message.predicate(): Boolean =
            this@Filter.checkFor(this) || otherFilter.checkFor(this)
    }

    operator fun not(): Filter = object : Filter {
        override fun Message.predicate(): Boolean = !this@Filter.checkFor(this)
    }

    class Custom(private val customPredicate: Message.() -> Boolean) : Filter {
        override fun Message.predicate(): Boolean = customPredicate()
    }

    object All : Filter {
        override fun Message.predicate(): Boolean = true
    }

    object Text : Filter {
        override fun Message.predicate(): Boolean = text != null && !text.startsWith("/")
    }

    object Command : Filter {
        override fun Message.predicate(): Boolean = text != null && text.startsWith("/")
    }

    object Reply : Filter {
        override fun Message.predicate(): Boolean = replyToMessage != null
    }

    object Forward : Filter {
        override fun Message.predicate(): Boolean = forwardDate != null
    }

    object Audio : Filter {
        override fun Message.predicate(): Boolean = audio != null
    }

    object Photo : Filter {
        override fun Message.predicate(): Boolean = photo != null && photo.isNotEmpty()
    }

    object Sticker : Filter {
        override fun Message.predicate(): Boolean = sticker != null
    }

    object Video : Filter {
        override fun Message.predicate(): Boolean = video != null
    }

    object VideoNote : Filter {
        override fun Message.predicate(): Boolean = videoNote != null
    }

    object Location : Filter {
        override fun Message.predicate(): Boolean = location != null
    }

    object Contact : Filter {
        override fun Message.predicate(): Boolean = contact != null
    }

    object Invoice : Filter {
        override fun Message.predicate(): Boolean = invoice != null
    }

    class Chat(private val chatId: Long) : Filter {
        override fun Message.predicate(): Boolean = chat.id == chatId
    }

    class User(private val userId: Long) : Filter {
        override fun Message.predicate(): Boolean = from?.id == userId
    }

    object Group : Filter {
        override fun Message.predicate(): Boolean =
            chat.type == "group" || chat.type == "supergroup"
    }

    object Private : Filter {
        override fun Message.predicate(): Boolean = chat.type == "private"
    }

    object Channel : Filter {
        override fun Message.predicate(): Boolean = chat.type == "channel"
    }
}
