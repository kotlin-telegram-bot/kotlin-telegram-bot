package com.github.kotlintelegrambot.entities

import java.io.File

sealed class TelegramFile {
    data class ByFileId(val fileId: String) : TelegramFile()
    data class ByUrl(val url: String) : TelegramFile()
    data class ByFile(val file: File) : TelegramFile()
    data class ByByteArray(val fileBytes: ByteArray, val filename: String? = null) : TelegramFile() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ByByteArray) return false

            if (!fileBytes.contentEquals(other.fileBytes)) return false
            if (filename != other.filename) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileBytes.contentHashCode()
            result = 31 * result + (filename?.hashCode() ?: 0)
            return result
        }
    }
}
