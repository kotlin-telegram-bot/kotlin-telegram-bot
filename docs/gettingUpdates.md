# Getting updates

(https://core.telegram.org/bots/api#getting-updates)

Telegram offers two different ways to listen to bot's updates:

* Using the `getUpdates` API operation. You can use this operation to proactively get updates from Telegram servers (bot implementations using this operation use to incorporate a polling mechanism to periodically ask for updates).  
* Setting up a webhook. With this approach, the updates are directly pushed from Telegram servers to the server where our bot is deployed.

Both methods cannot be used altogether, so you'll have to choose one of them depending on your needs.


## Using the `getUpdates` API operation 

Telegram exposes an API operation that let us query the latest updates correspondent to a given bot. For more details about this operation check out https://core.telegram.org/bots/api#getupdates

To receive updates in a near to realtime way you'll have to implement a polling mechanism to periodically ask Telegram for new updates. 

This library provides an API that lets you getting updates through the mentioned API operation with a built-in polling implementation.

You'll just need to create an instance of a bot and call to the `startPolling` method: 

```kotlin
bot {
    token = "YOUR_BOT_TOKEN"
    // bot implementation
}.startPolling()
```

For a more detailed example you can check the `dispatcher` and `echo` samples you can find in the `samples` folder of this project.

### Update consumption
Usually the `dispatcher` calls all the handlers which satisfy update parameters. If update was handled you can implicitly call `update.consume()` to mark the update as consumed so other handlers won't get it:  
```kotlin
bot { 
    dispatcher {
        text {  
            if (text == 'Hello World!') {
                update.consume()
            }  
        }
        text {
            // This handler will not get an update.
        }
    }
}
``` 


## Setting up a webhook

Telegram offers a way to receive updates through a webhook. You just have to indicate to Telegram the url where you want to receive the updates and Telegram will send an HTTPS POST with updates every time updates are received for a given bot.

For this matter, Telegram bots API provides several operations:

* `setWebhook`: this operation is used to indicate to Telegram the url where the updates for a given bot must be sent and other configuration parameters. 
* `deleteWebhook`: this operation is used to remove a previously set up webhook
* `getWebhook`: this operation is used to retrieve information about the currently set up webhook (url, max connections, etc...)

There are several requirements needed to start receiving updates through a webhook: 
* Your bot application must be running within a server accessible from internet, supporting IPv4 and SSL/TLS traffic 
* Your server needs to have a trusted SSL certificate or a self-signed one (in the case of a self-signed certificate, you'll have to send it to Telegram through the `setWebhook` API operation to let its servers trust on you)
* You need to execute the `setWebhook` API operation to indicate Telegram your webhook configuration.

For more detailed info about Telegram Bot API's webhooks check https://core.telegram.org/bots/webhooks

To use this feature from our library you'll have to do something like the next:

```kotlin
val bot = bot {
        token = "YOUR_BOT_TOKEN"
        // bot implementattion
        webhook {
            url = "YOUR_WEBHOOK_URL"
            // other config params if needed
        }
}
bot.startWebhook()

yourWebserver {
    routing("YOUR_WEBHOOK_URL/YOUR_BOT_TOKEN") {
        post {
           val receivedBody = httpCall.receiveText()
           bot.processUpdate(receivedBody)
        }
    }
}
```

You can check a sample of a bot using webhook feature in the `webhook` module of this project's `samples` folder. This example is using ktor framework with an embedded Netty server and a self-signed certificate. Please note that you can use your favourite server without any restriction.

If your server has a CA trusted SSL certificate all is fine. Otherwise, you'll needed to obtain one or generate a self-signed certificate. 

To try the sample provided in this project you can generate a self-signed certificate with the next commands (replacing the capital words with your own names):

```bash
# Generate the self-signed certificate in PEM format. Returns the certificate (.pem) and the private key (.key)
openssl req -newkey rsa:2048 -sha256 -nodes -keyout YOURPRIVATE.key -x509 -days 365 -out YOURPUBLIC.pem -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=YOURDOMAIN.EXAMPLE"

# Transform the certificate and the key to a format that Ktor undestands
openssl pkcs12 -export -out keystore.p12 -inkey YOURPRIVATE.pem -in YOURPUBLIC.pem -name ALIAS

# To generate a JKS file (also needed for Ktor)
keytool -importkeystore -alias $ALIAS -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore keystore.p12
```

Then update the `CertificateUtils` object with the things generated here




WARNING: please pay special attention to the ssl/tls configuration of your server. That's probably the most painful point of configuring webhooks. In the next link you will find a detailed guide about it https://core.telegram.org/bots/webhooks#ssl-tls-what-is-it-and-why-do-i-have-to-handle-this-for-a-webhoo 



