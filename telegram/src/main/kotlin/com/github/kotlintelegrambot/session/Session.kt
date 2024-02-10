package com.github.kotlintelegrambot.session

/**
 * Session represents a session object associated with a specific chat ID. It stores session properties as key-value pairs.
 * @property chatId The ID of the chat associated with the session.
 * @property sessionProperties A mutable map to store session properties.
 */
internal data class Session(private val chatId: Long) {
    private val sessionProperties: MutableMap<String, Any> = mutableMapOf()

    /**
     * Sets a session property with the specified key and value.
     * @param key The key of the property.
     * @param value The value of the property.
     */
    internal fun setProperty(key: String, value: Any) {
        sessionProperties[key] = value
    }

    /**
     * Retrieves the value of a session property with the specified key, or null if the property doesn't exist.
     * @param key The key of the property.
     * @return The value of the property, or null if the property doesn't exist.
     */
    internal fun getProperty(key: String): Any? {
        return sessionProperties[key]
    }

    /**
     * Returns all session properties as a map.
     * @return The session properties as a map.
     */
    internal fun getAllProperties(): Map<String, Any> {
        return sessionProperties.toMap()
    }

    /**
     * Removes a session property with the specified key.
     * @param key The key of the property to remove.
     */
    internal fun removeProperty(key: String) {
        sessionProperties.remove(key)
    }
}

