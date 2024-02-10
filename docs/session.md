# Session Management

The SessionManager is a singleton object that manages session data for different chat IDs in a Telegram bot. It provides methods to access, set, get and remove session properties.

⚠️ Session data is kept only in memory,  which means that all data will be lost when the process is terminated.

To use the SessionManager, you can import the necessary classes and create an instance of it. Here's an example:

```kotlin
val sessionManager = SessionManager
```

## Get Session Properties

You can retrieve the session properties for a specific chat ID using the getSessionProperties method. It returns the session properties as a map, or an empty map if no session exists for the given chat ID. Here's an example:

```kotlin
val properties = sessionManager.getSessionProperties(chatId)
```

## Set Session Property

You can set a session property for a specific chat ID using the setProperty method. If a session doesn't exist for the chat ID, a new session is created. Here's an example:

```kotlin
sessionManager.setProperty(chatId, "key", value)
```

## Get Session Property

You can retrieve the value of a session property with the specified key using the getProperty method. If a session doesn't exist for the chat ID, a new session is created. It returns the value of the property, or null if the property doesn't exist. Here's an example:

```kotlin
val value = sessionManager.getProperty(chatId, "key")
```

## Remove Session Property

You can remove a session property for a specific chat ID using the removeProperty method. Here's an example:

```kotlin
sessionManager.removeProperty(chatId, "key")
```
## Create Session

You can create a new session for a specific chat ID using the createSession method. It returns true if the session was successfully created, or false if a session with the same chat ID already exists. Here's an example:

```kotlin
val created = sessionManager.createSession(chatId)
```
## Remove Session

You can remove the session for a specific chat ID using the removeSession method. It returns true if the session was successfully removed, or false if the session doesn't exist. Here's an example:

```kotlin
val removed = sessionManager.removeSession(chatId)
```

