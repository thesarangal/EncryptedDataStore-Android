# EncryptedDataStore-Android: Secure Data Storage for Android

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Enhance the security of your Android applications with EncryptedDataStore-Android.** This library simplifies the process of encrypting data stored using Android's DataStore. It utilizes Cipher for strong encryption, protecting user data even if the device is compromised.  Easy to integrate, customize, and Kotlin-first.

## Key Features

* **Robust Encryption:** Employs Cipher for strong encryption (AES-256 recommended, but configurable).
* **DataStore Integration:** Built on top of Android's DataStore for persistent storage.
* **Simple API:** Intuitive methods for storing and retrieving encrypted data.
* **Type Safety:** Supports various data types (Gson serialization).
* **Customizable:**  Configure the encryption algorithm and key management.
* **Kotlin-First:** Designed with Kotlin in mind for seamless integration.
* **Jetpack Compose Compatible:** Works seamlessly with Jetpack Compose.

## Benefits

* **Enhanced Security:** Protects sensitive user information, API keys, and other critical data from unauthorized access.
* **Simplified Integration:** Easily integrate with existing Android projects.
* **Improved Compliance:** Helps meet data privacy regulations (e.g., GDPR).
* **Reduced Development Time:**  Focus on your app's core features, not security implementation.

## Getting Started

1. **Download and Add the `EncryptedDataStore.kt` File into your application**

2.  **Initialize EncryptedDataStore:**

Kotlin

```
val encryptedDataStore = EncryptedDataStore(context, "my_secure_data", cipherWrapperUtil) // cipherWrapperUtil is your CipherWrapperUtil instance

```

3.  **Store Encrypted Data:**

Kotlin

```
// Example: Storing a User object
val user = User("John Doe", "[email address removed]")
encryptedDataStore.storeValue("user_data", user)

```

4.  **Retrieve Encrypted Data:**

Kotlin

```
val userFlow: Flow<User?> = encryptedDataStore.readValue("user_data", User::class.java)

userFlow.collect { retrievedUser ->
    if (retrievedUser != null) {
        // Use the retrieved user object
        println("User: ${retrievedUser.name}")
    }
}

```

## Usage Examples

**Storing a String:**

Kotlin

```
encryptedDataStore.storeValue("api_key", "your_secret_key")

```

**Retrieving a String:**

Kotlin

```
val apiKeyFlow: Flow<String?> = encryptedDataStore.readValue("api_key", String::class.java)

```

**Storing a complex object:**

Kotlin

```
data class User(val name: String, val email: String)
encryptedDataStore.storeValue("user_profile", User("Alice", "example@rajatsarangal.in"))

```

**Retrieving a complex object:**

Kotlin

```
val userProfileFlow: Flow<User?> = encryptedDataStore.readValue("user_profile", User::class.java)

```

## CipherWrapperUtil

You'll need to provide your implementation of `CipherWrapperUtil` for handling the encryption/decryption using Cipher. This gives you flexibility in key management and algorithm selection. A basic example (using AES) is shown below, but you should adapt it to your specific security requirements. For a working example of a `CipherWrapperUtil` implementation, you can refer to this repository: [CipherWrapperUtil](https://github.com/thesarangal/CipherWrapperUtil).

Kotlin

```
class CipherWrapperUtil @Inject constructor(context: Context) { // Example, adapt to your needs
    // ...

    fun encrypt(alias: String, data: String): Pair<String, ByteArray> { /* ... */ }
    fun decrypt(alias: String, encrypted: ByteArray, iv: String): String { /* ... */ }

	companion object {  
	    const val TRANSFORMATION_ASYMMETRIC = "$KEY_ALGORITHM_RSA/$BLOCK_MODE_ECB/$ENCRYPTION_PADDING_RSA_PKCS1"  
		const val TRANSFORMATION_SYMMETRIC = "$KEY_ALGORITHM_AES/$BLOCK_MODE_CBC/$ENCRYPTION_PADDING_PKCS7"  
		const val IV_SEPARATOR = "]"  
	}
}

```

## Customization

-   **Encryption Algorithm:** You can configure the specific encryption algorithm used by your `CipherWrapperUtil`. AES-256 is recommended for strong security.
-   **Key Management:** Implement your own key management strategy within `CipherWrapperUtil`. Consider using the Android KeyStore for secure key storage.

## Contributing

Contributions are welcome! Feel free to submit pull requests or open issues.

## License

MIT License
