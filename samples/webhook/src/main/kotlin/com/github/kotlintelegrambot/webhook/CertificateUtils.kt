package com.github.kotlintelegrambot.webhook

import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

object CertificateUtils {
    val privateKeyPassword = "YOUR_PRIVATE_KEY_PASSWORD".toCharArray()
    val keyStorePassword = "YOUR_KEY_STORE_PASSWORD".toCharArray()
    const val keyAlias = "YOUR_CERT_KEY_ALIAS"
    const val certPath = "YOUR_CERT_PATH" // e.g.: /home/ruka/mycerts/cert.pem

    val keyStoreFile: File
        get() = File(keyStorePath).let { file ->
            if (file.exists() || file.isAbsolute)
                file
            else
                File(".", keyStorePath).absoluteFile
        }

    val keyStore: KeyStore
        get() = KeyStore.getInstance("JKS").apply {
            FileInputStream(keyStoreFile).use {
                load(it, privateKeyPassword)
            }

            requireNotNull(getKey(keyAlias, privateKeyPassword) == null) {
                "The specified key $keyAlias doesn't exist in the key store $keyStoreFile}"
            }
        }

    private const val keyStorePath = "YOUR_KEY_STORE_PATH" // e.g: /home/ruka/mycerts/keystore.jks
}
