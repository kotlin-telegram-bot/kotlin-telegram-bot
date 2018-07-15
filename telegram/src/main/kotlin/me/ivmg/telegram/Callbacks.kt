package me.ivmg.telegram

import me.ivmg.telegram.entities.Contact
import me.ivmg.telegram.entities.Location
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.errors.TelegramError

typealias HandleUpdate = (Bot, Update) -> Unit

typealias HandleError = (Bot, TelegramError) -> Unit

typealias CommandHandleUpdate = (Bot, Update, List<String>) -> Unit

typealias ContactHandleUpdate = (Bot, Update, Contact) -> Unit

typealias LocationHandleUpdate = (Bot, Update, Location) -> Unit
