# Logging

The library provides the possibility of configuring the logging in order to allow logging certain parts of the work performed by the library.

Nowadays, there are only two things that can be logged from the library side: the network requests and responses performed by the library, and the uncaught exceptions that are thrown from the handlers.

In order to configure the logging level you want, you have to set the `logLevel` property when building your bot instance. If you don't set it, it will default to not logging anything. You can do it in the next way: 

````kotlin
bot {
    logLevel = LogLevel.All()
}
````


The different available log levels, and the meaning of every one are shown below:

* `LogLevel.None` No logs.
* `LogLevel.All` Logs network requests, network responses and uncaught exceptions thrown in handlers execution. The log level for the network information can be also configured through the `networkLogLevel` property available in the `All` class (it'll default to `LogLevel.Network.Body`).  
* `LogLevel.Network` Logs network requests and responses. It has different levels.
  * `LogLevel.Network.None` No logs.
  * `LogLevel.Network.Basic` Logs requests and responses lines.
  * `LogLevel.Network.Headers` Logs requests and responses lines and their respective headers.
  * `LogLevel.Network.Body` Logs requests and responses lines and their respective headers and bodies (if present).
* `LogLevel.Error` Logs uncaught exceptions thrown in the handlers execution