package com.example.fructus.util

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureDatabaseKeyManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val MASTER_KEY_ALIAS = "fructus_master_key"
    private const val PREF_NAME = "secure_prefs"
    private const val PREF_KEY_PASSPHRASE = "db_passphrase_encrypted"
    private const val PREF_KEY_IV = "db_passphrase_iv"


    fun getOrCreatePassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val encryptedBase64 = prefs.getString(PREF_KEY_PASSPHRASE, null)
        val ivBase64 = prefs.getString(PREF_KEY_IV, null)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        return if (encryptedBase64 != null && ivBase64 != null) {
            // Already have an encrypted passphrase → decrypt it
            val encrypted = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val iv = Base64.decode(ivBase64, Base64.DEFAULT)

            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
            val decryptedBytes = cipher.doFinal(encrypted)
            decryptedBytes
        } else {
            // Create new passphrase
            val newPassphrase = java.util.UUID.randomUUID().toString().toByteArray(Charsets.UTF_8)

            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encrypted = cipher.doFinal(newPassphrase)
            val iv = cipher.iv

            // Save encrypted data
            prefs.edit()
                .putString(PREF_KEY_PASSPHRASE, Base64.encodeToString(encrypted, Base64.DEFAULT))
                .putString(PREF_KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT))
                .apply()

            newPassphrase

        }

    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = java.security.KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        // Return existing key if available
        keyStore.getKey(MASTER_KEY_ALIAS, null)?.let {
            return it as SecretKey
        }

        // Otherwise, generate a new one
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setIsStrongBoxBacked(false)
                }
            }
            .build()

        keyGen.init(spec)
        return keyGen.generateKey()
    }

}
