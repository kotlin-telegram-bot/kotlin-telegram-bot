package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleDocumentUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Document

class DocumentHandler(
    handleDocumentUpdate: HandleDocumentUpdate
) : MediaHandler<Document>(
    handleDocumentUpdate,
    DocumentHandlerFunctions::toMedia,
    DocumentHandlerFunctions::predicate
)

private object DocumentHandlerFunctions {

    fun toMedia(update: Update): Document {
        val document = update.message?.document
        checkNotNull(document)
        return document
    }

    fun predicate(update: Update): Boolean = update.message?.document != null
}
