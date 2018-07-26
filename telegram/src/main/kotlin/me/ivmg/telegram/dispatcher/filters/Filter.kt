package me.ivmg.telegram.dispatcher.filters

import me.ivmg.telegram.entities.Update

typealias Filter = (Update) -> Boolean

infix fun Filter.and(other: Filter): Filter = {
    this(it) && other(it)
}

infix fun Filter.or(other: Filter): Filter = {
    this(it) || other(it)
}