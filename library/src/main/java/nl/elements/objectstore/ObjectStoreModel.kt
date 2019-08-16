package nl.elements.objectstore

import nl.elements.objectstore.model.StringItem
import kotlin.properties.ReadWriteProperty

abstract class ObjectStoreModel(
    internal val store: ObjectStore
) {
    /**
     * Clear all items in this store
     */
    fun clear() {
        store.keys.forEach(store::remove)
    }

    /**
     * Delegate an item with type string
     * @param default value
     * @param key custom key
     */
    protected fun stringPref(
        default: String = "",
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, String> = StringItem(default, key)
}
