package me.ivmg.telegram

import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.errors.TelegramError

typealias HandleUpdate = (Bot, Update) -> Unit

typealias HandleError = (Bot, TelegramError) -> Unit