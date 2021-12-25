package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.RetrieveUpdatesError
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.Channel

internal class Updater(
    private val looper: Looper,
    private val updatesQueue: Channel<DispatchableObject>,
    private val apiClient: ApiClient,
    private val botTimeout: Int,
) {

    private val lastUpdateId = atomic<Long?>(null)

    internal fun launchPolling() {
        looper.launchLoop {
            val getUpdatesResult = apiClient.getUpdates(
                offset = lastUpdateId.value,
                limit = null,
                timeout = botTimeout,
                allowedUpdates = null,
            )

            getUpdatesResult.fold(
                ifSuccess = ::onUpdatesReceived,
                ifError = ::onErrorGettingUpdates
            )
        }
    }

    internal suspend fun awaitCancellation() {
        looper.awaitCancellation()
    }

    internal fun cancelPolling() {
        looper.cancelLoop()
    }

    private suspend fun onUpdatesReceived(updates: List<Update>) {
        if (updates.isEmpty()) {
            return
        }

        updates.forEach { updatesQueue.send(it) }

        lastUpdateId.value = updates.last().updateId + 1
    }

    private suspend fun onErrorGettingUpdates(error: TelegramBotResult.Error<List<Update>>) {
        val errorDescription: String? = when (error) {
            is TelegramBotResult.Error.HttpError -> "${error.httpCode} ${error.description}"
            is TelegramBotResult.Error.TelegramApi -> "${error.errorCode} ${error.description}"
            is TelegramBotResult.Error.InvalidResponse -> "${error.httpCode} ${error.httpStatusMessage}"
            is TelegramBotResult.Error.Unknown -> error.exception.message
        }

        val dispatchableError = RetrieveUpdatesError(
            errorDescription ?: "Error retrieving updates"
        )
        updatesQueue.send(dispatchableError)
    }
}
