# Proxy configuration

The library can route Telegram API traffic through a proxy with `Bot.Builder.proxy`.

If you need extra OkHttp setup, use `Bot.Builder.okHttpClientConfigurator`. The configurator is applied to the internal `OkHttpClient.Builder`, so you can attach proxy authentication, custom TLS settings, DNS, or any other supported OkHttp option.

## HTTP proxy with authenticator

```kotlin
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Response
import okhttp3.Route
import java.net.InetSocketAddress
import java.net.Proxy

fun main() {
    val proxyAuthenticator = Authenticator { _: Route?, response: Response ->
        response.request.newBuilder()
            .header("Proxy-Authorization", Credentials.basic("proxy-user", "proxy-password"))
            .build()
    }

    val bot = bot {
        token = "YOUR_API_KEY"
        proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("proxy.corp.example", 8080))
        okHttpClientConfigurator = {
            proxyAuthenticator(proxyAuthenticator)
        }
        dispatch {
            // ...
        }
    }
    bot.startPolling()
}
```

## Notes

- `proxy` still controls the transport route.
- `okHttpClientConfigurator` only affects the OkHttp client created for that bot instance.
- The configurator can be used for more than proxy auth, so the public API stays generic.

## Related

- [Logging](logging.md) — optional HTTP request/response logs when debugging proxy issues.
