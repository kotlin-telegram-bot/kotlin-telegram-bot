package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update

class CommandHandler(private val command: String, handler: HandleUpdate) : Handler(handler) {
    override val groupIdentifier: String = "CommandHandler"

    override fun checkUpdate(update: Update): Boolean {
        return (update.message?.text != null && update.message.text.startsWith("/")
            && update.message.text.drop(1).split(" ")[0].split("@")[0] == command)
    }
}