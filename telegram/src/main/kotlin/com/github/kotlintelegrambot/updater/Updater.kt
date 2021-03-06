package com.github.kotlintelegrambot.updater

import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.errors.RetrieveUpdatesError
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.types.DispatchableObject
import com.github.kotlintelegrambot.types.TelegramBotResult
import java.util.concurrent.BlockingQueue

internal class Updater(
    private val looper: Looper,
    private val updatesQueue: BlockingQueue<DispatchableObject>,
    private val apiClient: ApiClient,
    private val botTimeout: Int,
) {

    @Volatile private var lastUpdateId: Long? = null

    internal fun startPolling() {
        looper.loop {
            val getUpdatesResult = apiClient.getUpdates(
                offset = lastUpdateId,
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

    internal fun stopPolling() {
        looper.quit()
    }

    private fun onUpdatesReceived(updates: List<Update>) {
        if (updates.isEmpty()) {
            return
        }

        updates.forEach(updatesQueue::put)

        lastUpdateId = updates.last().updateId + 1
    }

    private fun onErrorGettingUpdates(error: TelegramBotResult.Error<List<Update>>) {
        val errorDescription: String? = when (error) {
            is TelegramBotResult.Error.HttpError -> "${error.httpCode} ${error.description}"
            is TelegramBotResult.Error.TelegramApi -> "${error.errorCode} ${error.description}"
            is TelegramBotResult.Error.InvalidResponse -> "${error.httpCode} ${error.httpStatusMessage}"
            is TelegramBotResult.Error.Unknown -> error.exception.message
        }

        val dispatchableError = RetrieveUpdatesError(
            errorDescription ?: "Error retrieving updates"
        )
        updatesQueue.put(dispatchableError)
    }
}
