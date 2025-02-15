import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import sarangal.packagemanager.utils.security.CipherWrapperUtil
import java.io.IOException
import javax.inject.Inject

/**
 * This class represents the implementation of DataStore with Cipher Cryptography.
 *
 * @author Rajat Sarangal
 * @since December 15, 2025
 * */
class EncryptedDataStore
@Inject constructor(
    val context: Context,
    dbName: String,
    val ciper: CipherWrapperUtil
) {

    // Create DataStore Preference
    val Context.dataStore by preferencesDataStore(dbName)

    // Security Key Alias for Cryptography
    val securityKeyAlias = "data-store"
    val bytesToStringSeparator = "|"

    // Gson Component
    val gson = Gson()

    /**
     * Store Any Object with Key
     *
     * @param key Key of DataStore
     * @param value Object
     * */
    suspend inline fun <reified T : Any> storeValue(key: String, value: T) {
        context.dataStore.secureEdit(value) { prefs, encryptedValue ->
            prefs[stringPreferencesKey(key)] = encryptedValue
        }
    }

    /**
     * Get Stored Object with Key
     *
     * @param key Pair of Key and Object
     * */
    inline fun <reified T> readValue(
        key: String, type: Class<T>
    ): Flow<T?> {
        return context.dataStore.data.catch {
            if (it is IOException) {
                Log.e(
                    javaClass.simpleName,
                    "getFromLocalStorage: ${key}: ${it.message}"
                )
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.secureMap(key, type) { preferences ->
            preferences[stringPreferencesKey(key)].orEmpty()
        }
    }

    /**
     * @return [Flow] of Object
     *
     * @param key Key of Store Object
     * @param fetchValue Callback for Preferences
     * */
    inline fun <reified T> Flow<Preferences>.secureMap(
        key: String,
        type: Class<T>,
        crossinline fetchValue: (value: Preferences) -> String
    ): Flow<T?> {
        return map {
            try {

                val data = fetchValue(it)
                val split = data.split(CipherWrapperUtil.IV_SEPARATOR.toRegex())

                if (split.size != 2) throw IllegalArgumentException("Passed data is incorrect. There was no IV specified with it.")

                val encryptedData = split[1]
                    .split(bytesToStringSeparator)
                    .map { string ->
                        string.toByte()
                    }
                    .toByteArray()

                val decryptedValue = ciper.decrypt(
                    securityKeyAlias, encryptedData, split[0]
                )
                gson.fromJson(decryptedValue, type)
            } catch (e: Exception) {
                Log.e(
                    javaClass.simpleName,
                    "secureMap: $key: ${e.message}"
                )
                null
            }
        }
    }

    /**
     * Store Data in DataStore
     *
     * @param value Object to be store
     * @param editStore callback for MutablePreference
     * */
    suspend inline fun <reified T> DataStore<Preferences>.secureEdit(
        value: T,
        crossinline editStore: (MutablePreferences, String) -> Unit
    ) {
        edit {
            val encryptedValue = ciper.encrypt(securityKeyAlias, gson.toJson(value))
            editStore.invoke(
                it,
                (encryptedValue.second +
                        CipherWrapperUtil.IV_SEPARATOR +
                        encryptedValue.first.joinToString(bytesToStringSeparator))
            )
        }
    }

    /**
     * Clear DataStore Data
     * */
    suspend fun clearDataStore() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
