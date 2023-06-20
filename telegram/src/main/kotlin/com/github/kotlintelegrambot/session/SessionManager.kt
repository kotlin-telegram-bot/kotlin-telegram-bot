package com.github.kotlintelegrambot.session

import java.util.concurrent.ConcurrentHashMap

/**
 * SessionManager is a singleton object that manages session data for different chat IDs.
 * It provides methods to access, set, get, create, and remove session and session properties.
 * ⚠️ Session data is kept only in memory,  which means that all data will be lost when the process is terminated.
 */
object SessionManager {
    private val sessions: MutableMap<Long, Session> = ConcurrentHashMap()

    /**
     * Returns the session properties for the specified chat ID.
     * @param chatId The ID of the chat.
     * @return The session properties as a map, or an empty map if no session exists for the given chat ID.
     */
    fun getSessionProperties(chatId: Long): Map<String, Any> {
        return sessions[chatId]?.getAllProperties() ?: emptyMap()
    }

    /**
     * Sets a session property for the specified chat ID.
     * @param chatId The ID of the chat
     * @param key: String - The key of the property
     * @param value: Any - The value of the property.
     */
    fun setProperty(chatId: Long, key: String, value: Any) {
        sessions.computeIfAbsent(chatId) { Session(chatId) }.setProperty(key, value)
    }


    /**
     * Retrieves the value of a session property with the specified key.
     * @param chatId The ID of the chat.
     * @param key The key of the property.
     * @return The value of the property, or `null` if the property doesn't exist or its value is `null`.
     */
    fun getProperty(chatId: Long, key: String): Any? {
        return sessions[chatId]?.getProperty(key)
    }

    /**
     * Removes a session property for the specified chat ID.
     * @param chatId The ID of the chat.
     * @param key The key of the property.
     */
    fun removeProperty(chatId: Long, key: String) {
        sessions[chatId]?.removeProperty(key)
    }

    /**
     * Creates a new session for the specified chat ID.
     * @param chatId The ID of the chat.
     * @return `true` if the session was successfully created, `false` if a session with the same chat ID already exists.
     */
    fun createSession(chatId: Long): Boolean {
        if (sessions.containsKey(chatId)) {
            return false
        }
        sessions[chatId] = Session(chatId)
        return true
    }

    /**
     * Removes the session for the specified chat ID.
     * @param chatId The ID of the chat.
     * @return `true` if the session was successfully removed, `false` if the session doesn't exist.
     */
    fun removeSession(chatId: Long): Boolean {
        return sessions.remove(chatId) != null
    }

}
