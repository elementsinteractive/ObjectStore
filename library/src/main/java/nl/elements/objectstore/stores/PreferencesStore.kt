package nl.elements.objectstore.stores

import android.content.SharedPreferences
import android.util.Base64
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.ObjectStore.Event.Removed
import nl.elements.objectstore.ObjectStore.Event.Updated
import nl.elements.objectstore.Transformer
import nl.elements.objectstore.writeToBytes

/**
 * A store that reads and writes its values from the given `SharedPreferences`. All values are stored as `String`.
 */

class PreferencesStore(
    private val preferences: SharedPreferences,
    override val converter: Converter = Converter.DEFAULT,
    override val transformer: Transformer = Transformer.DEFAULT
) : ObjectStore, ObjectStore.Base() {

    override val keys: Set<String>
        get() = preferences.all.keys

    override fun set(key: String, value: Any?) {
        writeToBytes(key, value)
            .let { Base64.encodeToString(it, 0) }
            .let { preferences.edit().putString(key, it).apply() }

        emit(Updated(key))
    }

    override fun <T : Any> get(key: String): T? =
        preferences
            .getString(key, null)!!
            .let { Base64.decode(it, 0) }
            .inputStream()
            .read(key)

    override fun contains(key: String): Boolean = preferences.contains(key)

    override fun remove(key: String) {
        if (preferences.edit().remove(key).commit()) {
            emit(Removed(key))
        }
    }

    fun toPreferences(): SharedPreferences = StorePreferences(this, preferences)

}

private class StorePreferences(
    private val store: ObjectStore,
    private val preferences: SharedPreferences
) : SharedPreferences {

    private val lock = Any()
    private val listeners = mutableMapOf<SharedPreferences.OnSharedPreferenceChangeListener, Disposable>()

    override fun contains(key: String?): Boolean =
        key?.let { store.contains(it) } ?: false

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = get(key, defValue)

    override fun getInt(key: String?, defValue: Int): Int = get(key, defValue)

    override fun getLong(key: String?, defValue: Long): Long = get(key, defValue)

    override fun getFloat(key: String?, defValue: Float): Float = get(key, defValue)

    override fun getString(key: String?, defValue: String): String? = get(key, defValue)

    override fun getStringSet(key: String?, defValues: MutableSet<String>): MutableSet<String>? = get(key, defValues)

    override fun getAll(): MutableMap<String, *> =
        store
            .keys
            .associateWith<String, Any?>(store::get)
            .toMutableMap()

    override fun edit(): SharedPreferences.Editor = StorePreferencesEditor(store, preferences.edit())

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        synchronized(lock) {
            listener?.let { listener ->
                Observable
                    .defer { store }
                    .subscribe { listener.onSharedPreferenceChanged(this, it.key) }
                    .let { listeners.put(listener, it) }
            }
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        synchronized(lock) {
            listener
                ?.let(listeners::remove)
                ?.dispose()
        }
    }

    private fun <T : Any> get(key: String?, defValue: T): T = key?.let { store.get<T>(it) } ?: defValue

}

private class StorePreferencesEditor(
    private val store: ObjectStore,
    private val editor: SharedPreferences.Editor
) : SharedPreferences.Editor by editor {

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = put(key, value)

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor = put(key, value)

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor = put(key, value)

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = put(key, value)

    override fun putString(key: String?, value: String?): SharedPreferences.Editor = put(key, value)

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = put(key, values)

    private fun <T> put(key: String?, value: T) = apply { key?.let { store[key] = value!! } }

}
