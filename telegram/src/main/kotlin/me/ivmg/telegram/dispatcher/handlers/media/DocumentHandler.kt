package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleDocumentUpdate
import me.ivmg.telegram.entities.Document
import me.ivmg.telegram.entities.Update

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