package nl.elements.objectstore

import nl.elements.objectstore.model.StringItem
import nl.elements.objectstore.model.StringNullableItem
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
     * Delegate an item with  string
     * @param default value
     * @param key custom key (optional)
     */
    protected fun stringItem(
        default: String = "",
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, String> = StringItem(default, key)

    /**
     * Delegate an item with nullable string
     * @param default value
     * @param key custom key (optional)
     */
    protected fun nullableStringItem(
        default: String? = null,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, String?> = StringNullableItem(default, key)
}
