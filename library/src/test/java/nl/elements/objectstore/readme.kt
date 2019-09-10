package nl.elements.objectstore

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.CryptoConfig
import nl.elements.objectstore.stores.DirectoryStore
import nl.elements.objectstore.stores.PreferencesStore
import nl.elements.objectstore.transformers.ConcealTransformer
import java.io.File

fun example(store: ObjectStore) {
    if ("id" !in store)
        store["id"] = 123L

    val id: Long = store["id"]

    store.remove("id")
}

fun observe(store: ObjectStore) {
    store
        .toObservable()
        .filter { event -> event.key == "id" }
        .map { event ->
            when (event) {
                is ObjectStore.Event.Updated -> store.get(event.key)
                is ObjectStore.Event.Removed -> -1L
            }
        }
        .subscribe(::println)
}

fun conceal(context: Context) {
    val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
    val crypto = AndroidConceal.get().createDefaultCrypto(keyChain)
    val prefs = context.getSharedPreferences("example", Context.MODE_PRIVATE)

    val store = PreferencesStore(
        preferences = prefs,
        transformer = ConcealTransformer(crypto)
    )
}

fun aggregate(directory: File, preferences: SharedPreferences) {
    // define the stores
    val pictures: ObjectStore = DirectoryStore(directory)
    val config: ObjectStore = PreferencesStore(preferences)

    // reduce them into one store
    val stores = listOf(pictures, config)
    val store: ReadableObjectStore = stores.reduce()

    // read from the stores
    val picture: Bitmap = store["selfie"]
    val token: String = store["debug"]
}

fun aggregateWithNamespace(directory: File, preferences: SharedPreferences) {
    // define the stores
    val pictures: ObjectStore = DirectoryStore(directory)
    val config: ObjectStore = PreferencesStore(preferences)

    // reduce them into one store
    val stores = mapOf("pictures" to pictures, "config" to config)
    val store: ReadableObjectStore = stores.reduceWithNamespace()

    // read from the stores
    val picture: Bitmap = store["pictures:selfie"]
    val token: String = store["config:debug"]
}