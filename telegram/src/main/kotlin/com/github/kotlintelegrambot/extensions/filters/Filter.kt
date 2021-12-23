package com.github.kotlintelegrambot.extensions.filters

import com.github.kotlintelegrambot.entities.Message

public interface Filter {
    public fun checkFor(message: Message): Boolean = message.predicate()
    public fun Message.predicate(): Boolean

    public infix fun and(otherFilter: Filter): Filter = object : Filter {
        override fun Message.predicate(): Boolean =
            this@Filter.checkFor(this) && otherFilter.checkFor(this)
    }

    public infix fun or(otherFilter: Filter): Filter = object : Filter {
        override fun Message.predicate(): Boolean =
            this@Filter.checkFor(this) || otherFilter.checkFor(this)
    }

    public operator fun not(): Filter = object : Filter {
        override fun Message.predicate(): Boolean = !this@Filter.checkFor(this)
    }

    public class Custom(private val customPredicate: Message.() -> Boolean) : Filter {
        override fun Message.predicate(): Boolean = customPredicate()
    }

    public object All : Filter {
        override fun Message.predicate(): Boolean = true
    }

    public object Text : Filter {
        override fun Message.predicate(): Boolean = text != null && !text.startsWith("/")
    }

    public object Command : Filter {
        override fun Message.predicate(): Boolean = text != null && text.startsWith("/")
    }

    public object Reply : Filter {
        override fun Message.predicate(): Boolean = replyToMessage != null
    }

    public object Forward : Filter {
        override fun Message.predicate(): Boolean = forwardDate != null
    }

    public object Audio : Filter {
        override fun Message.predicate(): Boolean = audio != null
    }

    public object Photo : Filter {
        override fun Message.predicate(): Boolean = photo != null && photo.isNotEmpty()
    }

    public object Sticker : Filter {
        override fun Message.predicate(): Boolean = sticker != null
    }

    public object Video : Filter {
        override fun Message.predicate(): Boolean = video != null
    }

    public object VideoNote : Filter {
        override fun Message.predicate(): Boolean = videoNote != null
    }

    public object Location : Filter {
        override fun Message.predicate(): Boolean = location != null
    }

    public object Contact : Filter {
        override fun Message.predicate(): Boolean = contact != null
    }

    public object Invoice : Filter {
        override fun Message.predicate(): Boolean = invoice != null
    }

    public class Chat(private val chatId: Long) : Filter {
        override fun Message.predicate(): Boolean = chat.id == chatId
    }

    public class User(private val userId: Long) : Filter {
        override fun Message.predicate(): Boolean = from?.id == userId
    }

    public object Group : Filter {
        override fun Message.predicate(): Boolean =
            chat.type == "group" || chat.type == "supergroup"
    }

    public object Private : Filter {
        override fun Message.predicate(): Boolean = chat.type == "private"
    }

    public object Channel : Filter {
        override fun Message.predicate(): Boolean = chat.type == "channel"
    }
}
