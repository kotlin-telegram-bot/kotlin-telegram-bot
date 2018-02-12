package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update

abstract class Handler(val handlerCallback: HandleUpdate) {
    abstract val groupIdentifier: String

    abstract fun checkUpdate(update: Update): Boolean
}